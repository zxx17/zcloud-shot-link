package com.zskj.account.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zskj.account.controller.request.TrafficPageRequest;
import com.zskj.account.controller.request.UseTrafficRequest;
import com.zskj.account.fegin.ProductFeignService;
import com.zskj.account.manager.TrafficManager;
import com.zskj.account.model.TrafficDO;
import com.zskj.account.service.TrafficService;
import com.zskj.account.vo.ProductVO;
import com.zskj.account.vo.TrafficVO;
import com.zskj.account.vo.UseTrafficVO;
import com.zskj.common.enums.BizCodeEnum;
import com.zskj.common.enums.EventMessageType;
import com.zskj.common.exception.BizException;
import com.zskj.common.interceptor.LoginInterceptor;
import com.zskj.common.model.EventMessage;
import com.zskj.common.model.LoginUser;
import com.zskj.common.util.JsonData;
import com.zskj.common.util.JsonUtil;
import com.zskj.common.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Xinxuan Zhuo
 * @version 2024/5/22
 * <p>
 *
 * </p>
 */
@Slf4j
@Service
public class TrafficServiceImpl implements TrafficService {

    @Autowired
    private TrafficManager trafficManager;

    @Autowired
    private ProductFeignService productFeignService;

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public boolean handleTrafficMessage(EventMessage eventMessage) {
        Long accountNo = eventMessage.getAccountNo();
        String messageType = eventMessage.getEventMessageType();
        // 支付则新增流量包
        if (EventMessageType.PRODUCT_ORDER_PAY.name().equalsIgnoreCase(messageType)) {

            //订单已经支付，新增流量

            String content = eventMessage.getContent();
            Map<String, Object> orderInfoMap = JsonUtil.json2Obj(content, Map.class);

            //还原订单商品信息
            String outTradeNo = (String) orderInfoMap.get("outTradeNo");
            Integer buyNum = (Integer) orderInfoMap.get("buyNum");
            String productStr = (String) orderInfoMap.get("product");
            ProductVO productVO = JsonUtil.json2Obj(productStr, ProductVO.class);
            log.info("商品信息:{}", productVO);


            //流量包有效期
            LocalDateTime expiredDateTime = LocalDateTime.now().plusDays(productVO.getValidDay());
            Date date = Date.from(expiredDateTime.atZone(ZoneId.systemDefault()).toInstant());


            //构建流量包对象
            TrafficDO trafficDO = TrafficDO.builder()
                    .accountNo(accountNo)
                    .dayLimit(productVO.getDayTimes() * buyNum)
                    .dayUsed(0)
                    .totalLimit(productVO.getTotalTimes())
                    .pluginType(productVO.getPluginType())
                    .level(productVO.getLevel())
                    .productId(productVO.getId())
                    .outTradeNo(outTradeNo)
                    .expiredDate(date).build();

            int rows = trafficManager.add(trafficDO);
            log.info("消费消息新增流量包:rows={},trafficDO={}", rows, trafficDO);
            return true;
        }
        // 注册则发放免费流量包
        else if (EventMessageType.TRAFFIC_FREE_INIT.name().equalsIgnoreCase(messageType)) {
            //发放免费流量包
            long productId = Long.parseLong(eventMessage.getBizId());

            JsonData jsonData = productFeignService.detail(productId);

            ProductVO productVO = jsonData.getData(new TypeReference<ProductVO>() {
            });
            //构建流量包对象
            TrafficDO trafficDO = TrafficDO.builder()
                    .accountNo(accountNo)
                    .dayLimit(productVO.getDayTimes())
                    .dayUsed(0)
                    .totalLimit(productVO.getTotalTimes())
                    .pluginType(productVO.getPluginType())
                    .level(productVO.getLevel())
                    .productId(productVO.getId())
                    .outTradeNo("free_init")
                    .expiredDate(new Date())
                    .build();

            trafficManager.add(trafficDO);
            return true;
        }
        return false;
    }

    @Override
    public Map<String, Object> pageAvailable(TrafficPageRequest request) {
        int size = request.getSize();
        int page = request.getPage();
        LoginUser loginUser = LoginInterceptor.threadLocal.get();

        IPage<TrafficDO> trafficDOIPage = trafficManager.pageAvailable(page, size, loginUser.getAccountNo());

        //获取流量包列表
        List<TrafficDO> records = trafficDOIPage.getRecords();

        List<TrafficVO> trafficVOList = records.stream().map(this::beanProcess).collect(Collectors.toList());

        Map<String, Object> pageMap = new HashMap<>(3);
        pageMap.put("total_record", trafficDOIPage.getTotal());
        pageMap.put("total_page", trafficDOIPage.getPages());
        pageMap.put("current_data", trafficVOList);

        return pageMap;

    }

    @Override
    public TrafficVO detail(long trafficId) {
        LoginUser loginUser = LoginInterceptor.threadLocal.get();
        TrafficDO trafficDO = trafficManager.findByIdAndAccountNo(trafficId, loginUser.getAccountNo());
        return this.beanProcess(trafficDO);
    }

    @Override
    public boolean deleteExpireTraffic() {
        return trafficManager.deleteExpireTraffic();
    }

    @Override
    public JsonData reduce(UseTrafficRequest useTrafficRequest) {
        // 获取当前用户账号
        Long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();
        //处理流量包，筛选出未更新流量包，当前使用的流量包
        UseTrafficVO useTrafficVO = processTrafficList(accountNo);

        return null;
    }

    /**
     * 处理流量包
     *
     * @param accountNo 账号
     * @return 未更新流量包，当前使用的流量包
     */
    private UseTrafficVO processTrafficList(Long accountNo) {
        // 全部流量包
        List<TrafficDO> trafficDOList = trafficManager.selectAvailableTraffics(accountNo);
        if (CollectionUtils.isEmpty(trafficDOList)) {
            // 免费流量包都没有说明出现了异常，这边需要进行核实补偿 TODO 根据账号查询订单记录有则补发...免费流量包补发
            throw new BizException(BizCodeEnum.TRAFFIC_EXCEPTION);
        }
        // 当前使用的流量包
        TrafficDO currentTrafficDO = null;
        // 当天可用总次数
        Integer dayTotalLeftTimes = 0;
        //未更新的流量包id列表
        List<Long> unUpdatedTrafficIds = new ArrayList<>();
        //今天日期
        String todayStr = TimeUtil.format(new Date(), "yyyy-MM-dd");
        for (TrafficDO trafficDO : trafficDOList) {
            String trafficUpdateDate = TimeUtil.format(trafficDO.getGmtModified(), "yyyy-MM-dd");
            // 判断流量包是否更新
            if (trafficUpdateDate.equalsIgnoreCase(todayStr)) {
                // 已经更新
                int dayLeftTimes = trafficDO.getDayLimit() - trafficDO.getDayUsed();
                dayTotalLeftTimes += dayLeftTimes;
                //选取当次使用流量包
                if (dayLeftTimes > 0 && currentTrafficDO == null) {
                    currentTrafficDO = trafficDO;
                }
            } else {
                // 未更新
                dayTotalLeftTimes += trafficDO.getDayLimit();
                unUpdatedTrafficIds.add(trafficDO.getId());
                if (currentTrafficDO == null) {
                    currentTrafficDO = trafficDO;
                }
            }
        }
        UseTrafficVO useTrafficVO = new UseTrafficVO(dayTotalLeftTimes, currentTrafficDO, unUpdatedTrafficIds);
        log.info("处理流量包:dayTotalLeftTimes={},currentTrafficDO={},unUpdatedTrafficIds={}", dayTotalLeftTimes, currentTrafficDO, unUpdatedTrafficIds);
        return useTrafficVO;
    }

    private TrafficVO beanProcess(TrafficDO trafficDO) {
        TrafficVO trafficVO = new TrafficVO();
        BeanUtils.copyProperties(trafficDO, trafficVO);
        return trafficVO;
    }
}
