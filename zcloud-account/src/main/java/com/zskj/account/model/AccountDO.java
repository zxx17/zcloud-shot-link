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
@TableName("account")
public class AccountDO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long accountNo;

    /**
     * 头像
     */
    private String headImg;

    /**
     * ⼿机号
     */
    private String phone;

    /**
     * 密码
     */
    private String pwd;

    /**
     * 盐，⽤于个⼈敏感信息处	理
     */
    private String secret;

    /**
     * 邮箱
     */
    private String mail;

    /**
     * ⽤户名
     */
    private String username;

    /**
     * 认证级别，DEFAULT，	REALNAME，ENTERPRISE，访问次数不⼀样
     */
    private String auth;

    private Date gmtCreate;

    private Date gmtModified;


}
