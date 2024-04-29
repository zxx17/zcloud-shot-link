package com.zskj.link.model;

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
 * @since 2024-04-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("link_group")
public class LinkGroupDO implements Serializable {

    private static final long serialVersionUID = 1L;


    private Long id;

    /**
     * 组名
     */
    private String title;

    /**
     * 账号唯⼀编	号
     */
    private Long accountNo;

    private Date gmtCreate;

    private Date gmtModified;


}
