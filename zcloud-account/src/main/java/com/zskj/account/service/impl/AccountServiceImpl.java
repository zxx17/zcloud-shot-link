package com.zskj.account.service.impl;

import com.zskj.account.model.AccountDO;
import com.zskj.account.mapper.AccountMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zskj.account.service.AccountService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Xinxuan Zhuo
 * @since 2024-04-22
 */
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, AccountDO> implements AccountService {

}
