package com.zskj.link.manager;

import com.zskj.common.enums.link.DomainTypeEnum;
import com.zskj.link.model.DomainDO;

import java.util.List;

/**
 * @author Xinxuan Zhuo
 * @version 2024/5/2
 * <p>
 *
 * </p>
 */
@SuppressWarnings("ALL")
public interface DomainManager {

    /**
     * 查找详情
     * @param id id
     * @param accountNO 账号id
     * @return
     */
    DomainDO findById(Long id,Long accountNO);


    /**
     * 查找详情
     * @param id id
     * @param domainTypeEnum 域名枚举
     * @return do
     */
    DomainDO findByDomainTypeAndID(Long id, DomainTypeEnum domainTypeEnum);


    /**
     * 新增
     * @param domainDO do
     * @return 影响行数
     */
    int addDomain(DomainDO domainDO);


    /**
     * 列举全部官方域名
     * @return list
     */
    List<DomainDO> listOfficialDomain();


    /**
     * 列举全部自定义域名
     * @param accountNo 账号id
     * @return list
     */
    List<DomainDO> listCustomDomain(Long accountNo);
}
