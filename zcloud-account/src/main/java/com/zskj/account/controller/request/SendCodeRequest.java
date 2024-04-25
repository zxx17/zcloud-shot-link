package com.zskj.account.controller.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author Xinxuan Zhuo
 * @version 2024/4/24
 * <p>
 * 请求验证码实体
 * </p>
 */

@Data
public class SendCodeRequest {

    /**
     * 验证码
     */
    @NotNull(message = "图形验证码不能为空")
    private String captcha;

    /**
     * 手机号或者邮箱
     */
    @NotNull(message = "手机号或者邮箱不能为空")
    private String to;

}
