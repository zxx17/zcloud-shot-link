package com.zskj.link.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Xinxuan Zhuo
 * @version 2024/5/2
 * <p>
 *
 * </p>
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class DomainVO implements Serializable {


    private Long id;

    /**
     * 用户自己绑定的域名
     */
    private Long accountNo;

    /**
     * 域名类型，自建custom, 官方offical
     */
    private String domainType;

    private String value;

    /**
     * 0是默认，1是禁用
     */
    private Integer del;

    private Date gmtCreate;

    private Date gmtModified;


}
