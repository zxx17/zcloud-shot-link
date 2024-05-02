package com.zskj.account.service;

import com.zskj.common.enums.account.SendCodeEnum;
import com.zskj.common.util.JsonData;

/**
 * @author Xinxuan Zhuo
 * @version 2024/4/23
 * <p>
 * 通知服务
 * </p>
 */

public interface NotifyService {

    /**
     * 发送验证码
     * @param sendCodeEnum flag
     * @param to target
     * @return jsonData
     */
    JsonData sendCode(SendCodeEnum sendCodeEnum, String to);

    /**
     * 验证码校验
     * @param sendCodeEnum flag
     * @param to target
     * @param code sms-code
     * @return jsonData
     */
    boolean checkCode(SendCodeEnum sendCodeEnum, String to, String code);
}
