package com.zskj.link.model;

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
 * @since 2024-05-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("domain")
public class DomainDO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * ⽤户⾃⼰绑	定的域名
     */
    private Long accountNo;

    /**
     * 域名类型，⾃	建custom, 官⽅offical
     */
    private String domainType;

    private String value;

    /**
     * 0是默认，1是禁⽤
     */
    private Integer del;

    private Date gmtCreate;

    private Date gmtModified;


}
