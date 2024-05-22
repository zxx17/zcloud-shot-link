package com.zskj.account.service;

import com.zskj.account.controller.request.TrafficPageRequest;
import com.zskj.account.vo.TrafficVO;
import com.zskj.common.model.EventMessage;

import java.util.Map;

/**
 * @author Xinxuan Zhuo
 * @version 2024/5/22
 * <p>
 *
 * </p>
 */

public interface TrafficService {

    /**
     * 处理流量包
     * @param eventMessage 时间消息
     * @return bool
     */
    boolean handleTrafficMessage(EventMessage eventMessage);


    /**
     * 分页查询当前用户可用流量包
     * @param request request
     * @return map
     */
    Map<String,Object> pageAvailable(TrafficPageRequest request);

    /**
     * 流量包详情
     * @param trafficId 流量包id
     * @return vo
     */
    TrafficVO detail(long trafficId);

}
