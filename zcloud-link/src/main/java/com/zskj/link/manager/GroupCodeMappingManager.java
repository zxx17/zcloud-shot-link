package com.zskj.link.manager;

import com.zskj.common.enums.link.ShortLinkStateEnum;
import com.zskj.link.model.GroupCodeMappingDO;

import java.util.Map;

/**
 * @author Xinxuan Zhuo
 * @version 2024/5/2
 * <p>
 * B端短链crud
 * </p>
 */

public interface GroupCodeMappingManager {

    /**
     * 查找详情
     * @param mappingId 路由表id
     * @param accountNo 账号id
     * @param groupId 分组id
     * @return  GroupCodeMappingDO
     */
    GroupCodeMappingDO findByGroupIdAndMappingId(Long mappingId, Long accountNo, Long groupId);


    /**
     * 新增
     * @param groupCodeMappingDO do
     * @return 影响行数
     */
    int add(GroupCodeMappingDO groupCodeMappingDO);


    /**
     * 根据短链码删除
     * @param groupCodeMappingDO do
     * @return 影响行数
     */
    int del(GroupCodeMappingDO groupCodeMappingDO);


    /**
     * 分页查找
     * @param page 页码
     * @param size 页大小
     * @param accountNo 账号id
     * @param groupId 分组id
     * @return map
     */
    Map<String,Object> pageShortLinkByGroupId(Integer page, Integer size, Long accountNo, Long groupId);


    /**
     * 更新短链码状态
     * @param accountNo 账号id
     * @param groupId 分组id
     * @param shortLinkCode 短链码
     * @param shortLinkStateEnum 状态枚举
     * @return 影响行数
     */
    int updateGroupCodeMappingState(Long accountNo, Long groupId, String shortLinkCode, ShortLinkStateEnum shortLinkStateEnum);

    /**
     * 根据短链码和分组id查找
     * @param shortLinkCode 短链码
     * @param id 分组id
     * @param accountNo 账号id
     * @return do
     */
    GroupCodeMappingDO findByCodeAndGroupId(String shortLinkCode, Long id, Long accountNo);

    /**
     * 更新短链mapping
     * @param groupCodeMappingDO do
     * @return int
     */
    int update(GroupCodeMappingDO groupCodeMappingDO);
}
