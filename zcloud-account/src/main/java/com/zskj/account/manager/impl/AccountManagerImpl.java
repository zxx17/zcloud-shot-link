package com.zskj.account.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zskj.account.manager.AccountManager;
import com.zskj.account.mapper.AccountMapper;
import com.zskj.account.model.AccountDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Xinxuan Zhuo
 * @version 2024/4/25
 * <p>
 *
 * </p>
 */

@Slf4j
@Service
public class AccountManagerImpl implements AccountManager {

    @Autowired
    private AccountMapper accountMapper;

    /**
     * 新增用户
     * @param accountDO 用户信息
     * @return 影响行数
     */
    @Override
    public int insert(AccountDO accountDO) {
        return accountMapper.insert(accountDO);
    }

    /**
     * 根据手机号查找用户
     * @param phone 手机号
     * @return 用户
     */
    @Override
    public List<AccountDO> findByPhone(String phone) {
        LambdaQueryWrapper<AccountDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AccountDO::getPhone, phone);
        return accountMapper.selectList(queryWrapper);
    }
}
