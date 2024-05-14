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
@TableName("product")
public class ProductDO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 商品标题
     */
    private String title;

    /**
     * 详情
     */
    private String detail;

    /**
     * 图⽚
     */
    private String img;

    /**
     * 产品层级：FIRST⻘铜、	SECOND⻩⾦、THIRD钻⽯
     */
    private String level;

    /**
     * 原	价
     */
    private BigDecimal oldAmount;

    /**
     * 现价
     */
    private BigDecimal amount;

    /**
     * ⼯具类型	short_link、qrcode
     */
    private String pluginType;

    /**
     * ⽇次数：短链类	型
     */
    private Integer dayTimes;

    /**
     * 总次数：活码	才有
     */
    private Integer totalTimes;

    /**
     * 有效天数
     */
    private Integer validDay;

    private Date gmtModified;

    private Date gmtCreate;


}
