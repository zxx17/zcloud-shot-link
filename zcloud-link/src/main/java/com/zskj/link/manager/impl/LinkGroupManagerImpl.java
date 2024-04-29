package com.zskj.link.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zskj.link.manager.LinkGroupManager;
import com.zskj.link.mapper.LinkGroupMapper;
import com.zskj.link.model.LinkGroupDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Xinxuan Zhuo
 * @version 2024/4/29
 * <p>
 *
 * </p>
 */

@Service
public class LinkGroupManagerImpl implements LinkGroupManager {

    @Autowired
    private LinkGroupMapper linkGroupMapper;

    /**
     * 添加分组
     *
     * @param linkGroupDO do
     * @return 影响行数
     */
    @Override
    public int add(LinkGroupDO linkGroupDO) {
        return linkGroupMapper.insert(linkGroupDO);
    }

    /**
     * 根据id删除分组
     *
     * @param groupId   分组id
     * @param accountNo 账号
     * @return 影响行数
     */
    @Override
    public int del(Long groupId, Long accountNo) {
        return linkGroupMapper.delete(new LambdaQueryWrapper<LinkGroupDO>()
                .eq(LinkGroupDO::getAccountNo, accountNo).eq(LinkGroupDO::getId, groupId));
    }

    /**
     * 根据id找详情
     *
     * @param groupId   分组id
     * @param accountNo 账号
     * @return 详情
     */
    @Override
    public LinkGroupDO detail(Long groupId, Long accountNo) {
        return linkGroupMapper.selectOne(new QueryWrapper<LinkGroupDO>()
                .eq("id", groupId).eq("account_no", accountNo));
    }

    /**
     * 列出用户全部分组
     *
     * @param accountNo 账号
     * @return 分组
     */
    @Override
    public List<LinkGroupDO> listAllGroup(Long accountNo) {
        return linkGroupMapper.selectList(new QueryWrapper<LinkGroupDO>()
                .eq("account_no", accountNo));
    }

    /**
     * 更新分组信息
     *
     * @param linkGroupDO do
     * @return 影响行数
     */
    @Override
    public int updateById(LinkGroupDO linkGroupDO) {
        return linkGroupMapper.update(linkGroupDO, new QueryWrapper<LinkGroupDO>()
                .eq("id", linkGroupDO.getId()).eq("account_no", linkGroupDO.getAccountNo()));
    }
}
