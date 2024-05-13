package com.zskj.link.service.impl;

import com.zskj.common.enums.link.DomainTypeEnum;
import com.zskj.common.enums.link.EventMessageType;
import com.zskj.common.enums.link.ShortLinkStateEnum;
import com.zskj.common.interceptor.LoginInterceptor;
import com.zskj.common.model.EventMessage;
import com.zskj.common.util.CommonUtil;
import com.zskj.common.util.IDUtil;
import com.zskj.common.util.JsonData;
import com.zskj.common.util.JsonUtil;
import com.zskj.link.component.ShortLinkComponent;
import com.zskj.link.config.rbtmq.RabbitMQConfig;
import com.zskj.link.controller.request.ShortLinkAddRequest;
import com.zskj.link.controller.request.ShortLinkPageRequest;
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
                    // 先判断短链码是否被使用
                    ShortLinkDO shortLinCodeDOInDB = shortLinkManager.findByShortLinkCode(shortLinkCode);
                    if (shortLinCodeDOInDB == null) {
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
        }catch (Exception e){
            log.error("短链码报错失败 返回错误:{}", e);
            throw new RuntimeException(e);
        }
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
