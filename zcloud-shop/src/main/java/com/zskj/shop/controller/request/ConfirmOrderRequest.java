package com.zskj.shop.controller.request;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author XinxuanZhuo
 */
@Data
public class ConfirmOrderRequest {

    /**
     * 商品id
     */
    @NotNull(message = "商品id不能为空")
    private Long productId;


    /**
     * 购买数量
     */
    @Min(value = 1, message = "购买数量不能小于1")
    private Integer buyNum;;


    /**
     * 终端类型
     */
    private String clientType;


    /**
     * 支付类型，微信-银行-支付宝
     */
    @NotNull(message = "支付类型不能为空")
    private String payType;


    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;

    /**
     * 订单实际支付价格
     */
    private BigDecimal payAmount;


    /**
     * 防重令牌
     */
    private String token;


    /**
     * 发票类型：0->不开发票；1->电子发票；2->纸质发票
     */
    private String billType;

    /**
     * 发票抬头
     */
    private String billHeader;

    /**
     * 发票内容
     */
    private String billContent;

    /**
     * 发票收票人电话
     */
    private String billReceiverPhone;

    /**
     * 发票收票人邮箱
     */
    private String billReceiverEmail;
}
