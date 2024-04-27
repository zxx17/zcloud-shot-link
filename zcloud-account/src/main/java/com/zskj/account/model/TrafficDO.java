package com.zskj.account.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author Xinxuan Zhuo
 * @since 2024-04-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("traffic")
public class TrafficDO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 每天限制多少	条，短链
     */
    private Integer dayLimit;

    /**
     * 当天⽤了多少条，	短链
     */
    private Integer dayUsed;

    /**
     * 总次数，活码	才⽤
     */
    private Integer totalLimit;

    /**
     * 账号
     */
    private Long accountNo;

    /**
     * 订单号
     */
    private String outTradeNo;

    /**
     * 产品层级：FIRST⻘铜、	SECOND⻩⾦、THIRD钻⽯
     */
    private String level;

    /**
     * 过期⽇期
     */
    private Date expiredDate;

    /**
     * 插件类型
     */
    private String pluginType;

    /**
     * 商品主键
     */
    private Long productId;

    private Date gmtCreate;

    private Date gmtModified;


}
