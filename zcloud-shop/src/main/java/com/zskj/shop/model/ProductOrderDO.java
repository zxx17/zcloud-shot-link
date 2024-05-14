package com.zskj.shop.model;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author Xinxuan Zhuo
 * @since 2024-05-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("product_order")
public class ProductOrderDO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 订单类型
     */
    private Long productId;

    /**
     * 商品标题
     */
    private String productTitle;

    /**
     * 商品单价
     */
    private BigDecimal productAmount;

    /**
     * 商品快照
     */
    private String productSnapshot;

    /**
     * 购买数ᰁ
     */
    private Integer buyNum;

    /**
     * 订	单唯⼀标识
     */
    private String outTradeNo;

    /**
     * NEW 未⽀	付订单,PAY已经⽀付订单,CANCEL超时取消订单
     */
    private String state;

    /**
     * 订单⽣	成时间
     */
    private Date createTime;

    /**
     * 订单总⾦额
     */
    private BigDecimal totalAmount;

    /**
     * 订	单实际⽀付价格
     */
    private BigDecimal payAmount;

    /**
     * ⽀付类	型，微信-银⾏-⽀付宝
     */
    private String payType;

    /**
     * 账号昵	称
     */
    private String nickname;

    /**
     * ⽤户id
     */
    private Long accountNo;

    /**
     * 0表示未删除，1表示已经	删除
     */
    private Integer del;

    /**
     * 更新时间
     */
    private Date gmtModified;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 发票	类型：0->不开发票；1->电⼦发票；2->纸质发票
     */
    private String billType;

    /**
     * 发	票抬头
     */
    private String billHeader;

    /**
     * 发票内容
     */
    private String billContent;

    /**
     * 发票收票⼈电话
     */
    private String billReceiverPhone;

    /**
     * 发票收票⼈邮箱
     */
    private String billReceiverEmail;


}
