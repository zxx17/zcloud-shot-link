package com.zskj.link.manager;


import com.zskj.link.model.LinkGroupDO;

import java.util.List;

/**
 * @author Xinxuan Zhuo
 * @version 2024/4/29
 * <p>
 *
 * </p>
 */

public interface LinkGroupManager {


    /**
     * 添加分组
     * @param linkGroupDO do
     * @return 影响行数
     */
    int add(LinkGroupDO linkGroupDO);

    /**
     * 根据id删除分组
     * @param groupId 分组id
     * @param accountNo 账号
     * @return 影响行数
     */
    int del(Long groupId, Long accountNo);

    /**
     * 根据id找详情
     * @param groupId 分组id
     * @param accountNo 账号
     * @return 详情
     */
    LinkGroupDO detail(Long groupId, Long accountNo);

    /**
     * 列出用户全部分组
     * @param accountNo 账号
     * @return 分组
     */
    List<LinkGroupDO> listAllGroup(Long accountNo);

    /**
     * 更新分组信息
     * @param linkGroupDO do
     * @return 影响行数
     */
    int updateById(LinkGroupDO linkGroupDO);
}
