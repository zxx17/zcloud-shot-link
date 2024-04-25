package com.zskj.account.manager;

import com.zskj.account.model.AccountDO;

import java.util.List;

/**
 * @author Xinxuan Zhuo
 * @version 2024/4/25
 * <p>
 *
 * </p>
 */

public interface AccountManager {

    /**
     * 新增用户
     * @param accountDO 用户信息
     * @return 影响行数
     */
    int insert(AccountDO accountDO);

    /**
     * 根据手机号查找用户
     * @param phone 手机号
     * @return 用户
     */
     List<AccountDO> findByPhone(String phone);
}
