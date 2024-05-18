package com.zskj.account.controller.request;

import lombok.Data;

import javax.validation.constraints.NotNull;


/**
 * @author Xinxuan Zhuo
 * @version 2024/4/25
 * <p>
 *
 * </p>
 */

@Data
public class AccountRegisterRequest {

    /**
     * 头像
     */
    private String headImg;

    /**
     * ⼿机号
     */
    @NotNull(message = "手机号不能为空")
    private String phone;

    /**
     * 密码
     */
    @NotNull(message = "密码不能为空")
    private String pwd;


    /**
     * 邮箱
     */
    private String mail;

    /**
     * ⽤户名
     */
    @NotNull(message = "用户名不能为空")
    private String username;

    /**
     * 短信验证码
     */
    @NotNull(message = "短信验证码不能为空")
    private String code;


}
