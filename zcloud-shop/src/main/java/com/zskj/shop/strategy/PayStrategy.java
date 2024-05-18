package com.zskj.shop.strategy;

import com.zskj.common.enums.shop.ProductOrderPayTypeEnum;
import com.zskj.shop.vo.PayInfoVO;

/**
 * @author Zhuo
 */
public interface PayStrategy {

    /**
     * @return 获取支付类型
     */
    ProductOrderPayTypeEnum getPayType();

    /**
     * 统一下单接口
     * @param payInfoVO 支付信息
     * @return 支付结果
     */
    String unifiedOrder(PayInfoVO payInfoVO);

    /**
     * 退款
     * @param payInfoVO 支付信息
     * @return 退款结果
     */
    String refundOrder(PayInfoVO payInfoVO);


    /**
     * 查询订单状态
     * @param payInfoVO 支付信息
     * @return 订单状态
     */
    String queryPayStatus(PayInfoVO payInfoVO);


    /**
     * 关闭订单
     * @param payInfoVO 支付信息
     * @return 订单状态
     */
    String closeOrder(PayInfoVO payInfoVO);

}
