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
public class AccountLoginRequest {

    @NotNull(message = "手机号不能为空")
    private String phone;

    @NotNull(message = "密码不能为空")
    private String pwd;
}
