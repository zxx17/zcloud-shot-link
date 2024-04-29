package com.zskj.link.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Xinxuan Zhuo
 * @version 2024/4/29
 * <p>
 *
 * </p>
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class LinkGroupVO implements Serializable {


    private Long id;

    /**
     * 组名
     */
    private String title;

    private Date gmtCreate;

    private Date gmtModified;


}

