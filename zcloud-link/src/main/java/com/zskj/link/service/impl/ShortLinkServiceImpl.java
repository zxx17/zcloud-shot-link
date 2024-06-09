package com.zskj.link.service.impl;

import com.zskj.common.constant.RedisKeyConstant;
import com.zskj.common.enums.BizCodeEnum;
import com.zskj.common.enums.link.DomainTypeEnum;
import com.zskj.common.enums.EventMessageType;
import com.zskj.common.enums.link.ShortLinkStateEnum;
import com.zskj.common.interceptor.LoginInterceptor;
import com.zskj.common.model.EventMessage;
import com.zskj.common.util.CommonUtil;
import com.zskj.common.util.IDUtil;
import com.zskj.common.util.JsonData;
import com.zskj.common.util.JsonUtil;
import com.zskj.link.component.ShortLinkComponent;
import com.zskj.link.config.rbtmq.RabbitMQConfig;
import com.zskj.link.controller.request.*;
import com.zskj.link.fegin.TrafficFeignService;
import com.zskj.link.manager.DomainManager;
import com.zskj.link.manager.GroupCodeMappingManager;
import com.zskj.link.manager.LinkGroupManager;
import com.zskj.link.manager.ShortLinkManager;
import com.zskj.link.model.DomainDO;
import com.zskj.link.model.GroupCodeMappingDO;
import com.zskj.link.model.LinkGroupDO;
import com.zskj.link.model.ShortLinkDO;
import com.zskj.link.service.ShortLinkService;
import com.zskj.link.vo.ShortLinkVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Xinxuan Zhuo
 * @version 2024/5/2
 * <p>
 *
 * </p>
 */
@SuppressWarnings("ALL")
@Slf4j
@Service
public class ShortLinkServiceImpl implements ShortLinkService {


    @Autowired
    private ShortLinkManager shortLinkManager;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitMQConfig rabbitMQConfig;

    @Autowired
    private DomainManager domainManager;

    @Autowired
    private LinkGroupManager linkGroupManager;

    @Autowired
    private ShortLinkComponent shortLinkComponent;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    private GroupCodeMappingManager groupCodeMappingManager;

    @Autowired
    private TrafficFeignService trafficFeignService;


    @Override
    public ShortLinkVO parseShortLinkCode(String shortLinkCode) {
        ShortLinkDO shortLinkDO = shortLinkManager.findByShortLinkCode(shortLinkCode);
        if (shortLinkDO == null) {
            return null;
        }
        ShortLinkVO shortLinkVO = new ShortLinkVO();
        BeanUtils.copyProperties(shortLinkDO, shortLinkVO);
        return shortLinkVO;
    }

    @Override
    public JsonData createShortLink(ShortLinkAddRequest request) {
        Long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();

        //需要预先检查下是否有足够多的可以进行创建
        String cacheKey = String.format(RedisKeyConstant.DAY_TOTAL_TRAFFIC, accountNo);
        // 使用lua脚本 检查key是否存在，然后递减，是否大于等于0
        // 如果key不存在，则未使用过，lua返回值是0； 新增流量包的时候，不用重新计算次数，直接删除key,消费的时候回计算更新
        String script = "if redis.call('get',KEYS[1]) then return redis.call('decr',KEYS[1]) else return 0 end";
        Long leftTimes = redisTemplate.execute(new DefaultRedisScript<>(script, Long.class), Arrays.asList(cacheKey), "");
        log.info(">>>>>今日流量预扣减完剩余次数:{}", leftTimes);
        //流量包不足
        if (leftTimes < 0) {
            return JsonData.buildResult(BizCodeEnum.TRAFFIC_REDUCE_FAIL);
        }
        // 预扣减成功（>=0  等于0的情况可能是新增流量包或则惰性更新）
        // 防止短链码重复，也解决长-短链码一对多的问题
        String newOriginalUrl = CommonUtil.addUrlPrefix(request.getOriginalUrl());
        request.setOriginalUrl(newOriginalUrl);

        // 构建mq消息
        EventMessage eventMessage = EventMessage.builder().accountNo(accountNo)
                .content(JsonUtil.obj2Json(request))
                .messageId(IDUtil.geneSnowFlakeId().toString())
                .eventMessageType(EventMessageType.SHORT_LINK_ADD.name())
                .build();
        // 发送mq消息
        rabbitTemplate.convertAndSend(rabbitMQConfig.getShortLinkEventExchange(),
                rabbitMQConfig.getShortLinkAddRoutingKey(),
                eventMessage);

        return JsonData.buildSuccess();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handlerAddShortLink(EventMessage eventMessage) {
        try {
            if (log.isInfoEnabled()) {
                log.info("开始处理新增短链事件：{}", eventMessage);
            }
            Long accountNo = eventMessage.getAccountNo();
            String messageType = eventMessage.getEventMessageType();
            // 还原request实体
            ShortLinkAddRequest addRequest = JsonUtil.json2Obj(eventMessage.getContent(), ShortLinkAddRequest.class);
            // TODO 对原始链接的校验 违法网站不进行校验 （利用es）调用es的接口，查询源地址是否是违禁地址
            // 校验域名合法（这里其实就是判空）TODO 未来允许用户自定义域名
            DomainDO domainDO = checkDomain(addRequest.getDomainType(), addRequest.getDomainId(), accountNo);
            // 校验组名合法（这里其实就是判空和越权检测）
            LinkGroupDO linkGroupDO = checkLinkGroup(addRequest.getGroupId(), accountNo);

            //生成长链摘要
            String originalUrlDigest = CommonUtil.MD5(addRequest.getOriginalUrl());

            //短链码重复标记
            boolean duplicateCodeFlag = false;

            //生成短链码
            String shortLinkCode = shortLinkComponent.createShortLinkCode(addRequest.getOriginalUrl());

            // TODO 加锁 先使用有问题后续进行改进
            //key1是短链码，ARGV[1]是accountNo,ARGV[2]是过期时间
            String script = "if redis.call('EXISTS',KEYS[1])==0 then redis.call('set',KEYS[1],ARGV[1]); redis.call('expire',KEYS[1],ARGV[2]); return 1;" +
                    " elseif redis.call('get',KEYS[1]) == ARGV[1] then return 2;" +
                    " else return 0; end;";
            Long result = redisTemplate.execute(
                    new DefaultRedisScript<>(script, Long.class),
                    Collections.singletonList(shortLinkCode),
                    accountNo,
                    100);

            // 加锁成功逻辑
            if (result > 0) {
                // C端处理
                if (EventMessageType.SHORT_LINK_ADD_LINK.name().equalsIgnoreCase(messageType)) {
                    log.info(">>>>>C端处理新增短链事件：{}", eventMessage);
                    // 先判断短链码是否被使用
                    ShortLinkDO shortLinCodeDOInDB = shortLinkManager.findByShortLinkCode(shortLinkCode);
                    if (shortLinCodeDOInDB == null) {
                        // 远程调用账号服务扣减流量包
                        boolean reduceFlag = reduceTraffic(eventMessage, shortLinkCode);
                        // 扣减失败
                        if (!reduceFlag) {
                            return false;
                        }
                        // 扣减成功，新增短链码
                        ShortLinkDO shortLinkDO = ShortLinkDO.builder()
                                .accountNo(accountNo).code(shortLinkCode)
                                .title(addRequest.getTitle()).originalUrl(addRequest.getOriginalUrl())
                                .domain(domainDO.getValue()).groupId(linkGroupDO.getId())
                                .expired(addRequest.getExpired()).sign(originalUrlDigest)
                                .state(ShortLinkStateEnum.ACTIVE.name()).del(0).build();
                        shortLinkManager.addShortLink(shortLinkDO);
                        return true;
                    } else {
                        log.error("C端短链码重复:{}", eventMessage);
                        duplicateCodeFlag = true;
                    }
                } // B端处理
                else if (EventMessageType.SHORT_LINK_ADD_MAPPING.name().equalsIgnoreCase(messageType)) {
                    log.info(">>>>>B端处理新增短链事件：{}", eventMessage);
                    // 判断b端短链码是否重复
                    GroupCodeMappingDO groupCodeMappingDOInDB = groupCodeMappingManager.findByCodeAndGroupId(shortLinkCode, linkGroupDO.getId(), accountNo);
                    if (groupCodeMappingDOInDB == null) {
                        GroupCodeMappingDO groupCodeMappingDO = GroupCodeMappingDO.builder()
                                .accountNo(accountNo).code(shortLinkCode).title(addRequest.getTitle())
                                .originalUrl(addRequest.getOriginalUrl())
                                .domain(domainDO.getValue()).groupId(linkGroupDO.getId())
                                .expired(addRequest.getExpired()).sign(originalUrlDigest)
                                .state(ShortLinkStateEnum.ACTIVE.name()).del(0).build();
                        groupCodeMappingManager.add(groupCodeMappingDO);
                        return true;
                    } else {
                        log.error("B端短链码重复:{}", eventMessage);
                        duplicateCodeFlag = true;
                    }
                }

            } else {
                //加锁失败，自旋100毫秒，再调用； 失败的可能是短链码已经被占用，需要重新生成
                log.error("加锁失败:{}", eventMessage);
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    log.error("系统异常");
                }
                duplicateCodeFlag = true;
            }

            // 处理重复标记
            if (duplicateCodeFlag) {
                // 对短链码进行版本升级 + 1
                String newOriginalUrl = CommonUtil.addUrlPrefixVersion(addRequest.getOriginalUrl());
                addRequest.setOriginalUrl(newOriginalUrl);
                eventMessage.setContent(JsonUtil.obj2Json(addRequest));
                log.warn("短链码报错失败，重新生成...CAS...:{}", eventMessage);
                handlerAddShortLink(eventMessage);
            }
            return false;
        } catch (Exception e) {
            log.error("短链码报错失败 返回错误:{}", e);
            throw new RuntimeException(e);
        }
    }


    @Override
    public boolean handleDelShortLink(EventMessage eventMessage) {
        try {
            if (log.isInfoEnabled()) {
                log.info("开始处理删除短链事件：{}", eventMessage);
            }
            // 获取当前账户
            Long accountNo = eventMessage.getAccountNo();
            // 获取当前事件类型
            String messageType = eventMessage.getEventMessageType();
            // 还原request实体
            ShortLinkDelRequest delRequest = JsonUtil.json2Obj(eventMessage.getContent(), ShortLinkDelRequest.class);
            // 类型分支
            if (EventMessageType.SHORT_LINK_DEL_LINK.name().equalsIgnoreCase(messageType)) {
                // link
                ShortLinkDO shortLinkDO = ShortLinkDO.builder()
                        .code(delRequest.getCode())
                        .accountNo(accountNo).build();
                int rows = shortLinkManager.logicDelShortLink(shortLinkDO);
                log.info("删除C端短链:{}", rows);
                return true;
            } else if (EventMessageType.SHORT_LINK_DEL_MAPPING.name().equalsIgnoreCase(messageType)) {
                // mapping
                GroupCodeMappingDO groupCodeMappingDO = GroupCodeMappingDO.builder()
                        .id(delRequest.getMappingId()).accountNo(accountNo)
                        .groupId(delRequest.getGroupId()).code(delRequest.getCode()).build();

                int rows = groupCodeMappingManager.del(groupCodeMappingDO);
                log.info("删除B端短链:{}", rows);
                return true;
            }
        } catch (Exception e) {
            log.error("短链码删除报错失败 返回错误:{}", e);
            throw new RuntimeException(e);
        }
        return false;
    }

    @Override
    public boolean handleUpdateShortLink(EventMessage eventMessage) {
        try {
            if (log.isInfoEnabled()) {
                log.info("开始处理更新短链事件：{}", eventMessage);
            }
            Long accountNo = eventMessage.getAccountNo();
            String messageType = eventMessage.getEventMessageType();
            ShortLinkUpdateRequest updateRequest = JsonUtil.json2Obj(eventMessage.getContent(), ShortLinkUpdateRequest.class);

            // 校验域名
            DomainDO domainDO = checkDomain(updateRequest.getDomainType(), updateRequest.getDomainId(), accountNo);
            //校验组名
            LinkGroupDO linkGroupDO = checkLinkGroup(updateRequest.getGroupId(), accountNo);

            // 分支处理
            if (EventMessageType.SHORT_LINK_UPDATE_LINK.name().equalsIgnoreCase(messageType)) {
                // link
                ShortLinkDO shortLinkDO = ShortLinkDO.builder()
                        .code(updateRequest.getCode())
                        .title(updateRequest.getTitle())
                        .domain(domainDO.getValue())
                        .accountNo(accountNo).build();
                int rows = shortLinkManager.update(shortLinkDO);
                log.info("更新C端短链，rows={}", rows);
                return true;
            } else if (EventMessageType.SHORT_LINK_UPDATE_MAPPING.name().equalsIgnoreCase(messageType)) {
                // mapping
                GroupCodeMappingDO groupCodeMappingDO = GroupCodeMappingDO.builder()
                        .id(updateRequest.getMappingId())
                        .groupId(updateRequest.getGroupId())
                        .accountNo(accountNo)
                        .title(updateRequest.getTitle())
                        .domain(domainDO.getValue())
                        .code(updateRequest.getCode())
                        .build();
                int rows = groupCodeMappingManager.update(groupCodeMappingDO);
                log.info("更新B端短链，rows={}", rows);
                return true;
            }
        } catch (Exception e) {
            log.error("短链码更新报错失败 返回错误:{}", e);
            throw new RuntimeException(e);
        }
        return false;
    }

    @Override
    public Map<String, Object> pageByGroupId(ShortLinkPageRequest request) {
        // 获取当前用户
        Long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();
        Map<String, Object> map = groupCodeMappingManager.pageShortLinkByGroupId(request.getPage(),
                request.getSize(),
                accountNo,
                request.getGroupId());
        return map;
    }


    @Override
    public JsonData delShortLink(ShortLinkDelRequest request) {
        // 获取当前用户
        Long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();
        // 构建mq消息
        EventMessage eventMessage = EventMessage.builder().accountNo(accountNo)
                .content(JsonUtil.obj2Json(request))
                .messageId(IDUtil.geneSnowFlakeId().toString())
                .eventMessageType(EventMessageType.SHORT_LINK_DEL.name())
                .build();
        // 发送mq消息
        rabbitTemplate.convertAndSend(rabbitMQConfig.getShortLinkEventExchange(),
                rabbitMQConfig.getShortLinkDelRoutingKey(),
                eventMessage);

        return JsonData.buildSuccess();
    }

    @Override
    public JsonData updateShortLink(ShortLinkUpdateRequest request) {
        // 获取当前用户
        Long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();
        // 构建mq消息
        EventMessage eventMessage = EventMessage.builder().accountNo(accountNo)
                .content(JsonUtil.obj2Json(request))
                .messageId(IDUtil.geneSnowFlakeId().toString())
                .eventMessageType(EventMessageType.SHORT_LINK_UPDATE.name())
                .build();
        // 发送mq消息
        rabbitTemplate.convertAndSend(rabbitMQConfig.getShortLinkEventExchange(),
                rabbitMQConfig.getShortLinkUpdateRoutingKey(),
                eventMessage);

        return JsonData.buildSuccess();
    }


    /**
     * 调用账号服务---扣减流量包
     */
    private boolean reduceTraffic(EventMessage eventMessage, String shortLinkCode) {
        UseTrafficRequest request = new UseTrafficRequest();
        request.setBizId(shortLinkCode);
        JsonData jsonData = trafficFeignService.useTraffic(request);
        if (jsonData.getCode() != 0) {
            log.error("流量包不足，扣减失败:{}", eventMessage);
            return false;
        }
        return true;
    }

    /**
     * 校验域名
     *
     * @param domainType
     * @param domainId
     * @param accountNo
     * @return
     */
    private DomainDO checkDomain(String domainType, Long domainId, Long accountNo) {
        DomainDO domainDO;
        if (DomainTypeEnum.CUSTOM.name().equalsIgnoreCase(domainType)) {
            domainDO = domainManager.findById(domainId, accountNo);
        } else {
            domainDO = domainManager.findByDomainTypeAndID(domainId, DomainTypeEnum.OFFICIAL);
        }
        Assert.notNull(domainDO, "短链域名不合法");
        return domainDO;
    }

    /**
     * 校验组名
     *
     * @param groupId
     * @param accountNo
     * @return
     */
    private LinkGroupDO checkLinkGroup(Long groupId, Long accountNo) {
        LinkGroupDO linkGroupDO = linkGroupManager.detail(groupId, accountNo);
        Assert.notNull(linkGroupDO, "组名不合法");
        return linkGroupDO;
    }

}
