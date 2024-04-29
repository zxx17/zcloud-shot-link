package com.zskj.account.service;

import com.zskj.account.controller.request.AccountLoginRequest;
import com.zskj.account.controller.request.AccountRegisterRequest;
import com.zskj.common.util.JsonData;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Xinxuan Zhuo
 * @since 2024-04-22
 */
public interface AccountService {

    /**
     * 用户注册
     * @param request 注册表单
     * @return res
     */
    JsonData register(AccountRegisterRequest request);

    /**
     * 用户登录
     * @param request 登录表单数据
     * @return res
     */
    JsonData login(AccountLoginRequest request);
}
