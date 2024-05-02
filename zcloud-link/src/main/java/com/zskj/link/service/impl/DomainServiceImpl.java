package com.zskj.link.service.impl;

import com.zskj.common.interceptor.LoginInterceptor;
import com.zskj.link.manager.DomainManager;
import com.zskj.link.model.DomainDO;
import com.zskj.link.service.DomainService;
import com.zskj.link.vo.DomainVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Xinxuan Zhuo
 * @version 2024/5/2
 * <p>
 *
 * </p>
 */

@Slf4j
@Service
public class DomainServiceImpl implements DomainService {

    @Autowired
    private DomainManager domainManager;


    @Override
    public List<DomainVO> listAll() {
        // 获取当前账户
        Long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();
        // 账户自建
        List<DomainDO> customDomainList = domainManager.listCustomDomain(accountNo);
        // 官方
        List<DomainDO> officialDomainList = domainManager.listOfficialDomain();
        customDomainList.addAll(officialDomainList);
        return customDomainList.stream().map(this::beanProcess).collect(Collectors.toList());
    }

    private DomainVO beanProcess(DomainDO domainDO){
        DomainVO domainVO = new DomainVO();
        BeanUtils.copyProperties(domainDO,domainVO);
        return domainVO;
    }

}
