package com.zskj.shop.service;

import com.zskj.common.enums.shop.ProductOrderPayTypeEnum;
import com.zskj.common.model.EventMessage;
import com.zskj.common.util.JsonData;
import com.zskj.shop.controller.request.ConfirmOrderRequest;
import com.zskj.shop.controller.request.ProductOrderPageRequest;

import java.util.Map;

/**
 * @author Xinxuan Zhuo
 * @version 2024/5/14
 * <p>
 *
 * </p>
 */

public interface ProductOrderService {

    /**
     * 分页查询订单
     * @param request req
     * @return jsonData
     */
    Map<String, Object> page(ProductOrderPageRequest request);

    /**
     * 查询订单状态
     * @param outTradeNo 订单号
     * @return status
     */
    String queryProductOrderState(String outTradeNo);

    /**
     * 下单
     * @param request req
     * @return resp
     */
    JsonData confirmOrder(ConfirmOrderRequest request);

    /**
     * 处理订单
     * @param eventMessage event
     * @return bool
     */
    boolean handleProductOrderMessage(EventMessage eventMessage);

    /**
     * 处理微信回调通知
     * @param payType 支付类型
     * @param paramsMap 支付回调参数
     */
    JsonData processOrderCallbackMsg(ProductOrderPayTypeEnum payType, Map<String, String> paramsMap);
}
