package com.zskj.link.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zskj.common.enums.link.DomainTypeEnum;
import com.zskj.link.manager.DomainManager;
import com.zskj.link.mapper.DomainMapper;
import com.zskj.link.model.DomainDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Xinxuan Zhuo
 * @version 2024/5/2
 * <p>
 *
 * </p>
 */

@Slf4j
@Service
@SuppressWarnings("ALL")
public class DomainManagerImpl implements DomainManager {


    @Autowired
    private DomainMapper domainMapper;

    @Override
    public DomainDO findById(Long id, Long accountNo) {
        return domainMapper.selectOne(new QueryWrapper<DomainDO>().eq("id", id).eq("account_no", accountNo));
    }

    @Override
    public DomainDO findByDomainTypeAndID(Long id, DomainTypeEnum domainTypeEnum) {
        return domainMapper.selectOne(new QueryWrapper<DomainDO>().eq("id", id).eq("domain_type", domainTypeEnum.name()));
    }

    @Override
    public int addDomain(DomainDO domainDO) {
        return domainMapper.insert(domainDO);
    }

    @Override
    public List<DomainDO> listOfficialDomain() {
        return domainMapper.selectList(new QueryWrapper<DomainDO>().eq("domain_type", DomainTypeEnum.OFFICIAL.name()));
    }

    @Override
    public List<DomainDO> listCustomDomain(Long accountNo) {
        return domainMapper.selectList(new QueryWrapper<DomainDO>()
                .eq("domain_type", DomainTypeEnum.CUSTOM.name())
                .eq("account_no", accountNo));
    }
}
