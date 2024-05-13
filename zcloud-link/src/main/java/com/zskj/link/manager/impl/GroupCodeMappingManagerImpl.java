package com.zskj.link.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zskj.common.enums.link.ShortLinkStateEnum;
import com.zskj.link.manager.GroupCodeMappingManager;
import com.zskj.link.mapper.GroupCodeMappingMapper;
import com.zskj.link.model.GroupCodeMappingDO;
import com.zskj.link.vo.GroupCodeMappingVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
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
public class GroupCodeMappingManagerImpl implements GroupCodeMappingManager {

    @Autowired
    private GroupCodeMappingMapper groupCodeMappingMapper;

    @Override
    public GroupCodeMappingDO findByGroupIdAndMappingId(Long mappingId, Long accountNo, Long groupId) {

        return groupCodeMappingMapper.selectOne(new QueryWrapper<GroupCodeMappingDO>()
                .eq("id", mappingId).eq("account_no", accountNo)
                .eq("group_id", groupId).eq("del", 0));
    }

    @Override
    public int add(GroupCodeMappingDO groupCodeMappingDO) {
        return groupCodeMappingMapper.insert(groupCodeMappingDO);
    }

    @Override
    public int del(GroupCodeMappingDO groupCodeMappingDO) {
        return groupCodeMappingMapper.update(null, new UpdateWrapper<GroupCodeMappingDO>()
                .eq("code", groupCodeMappingDO.getCode()).eq("account_no", groupCodeMappingDO.getAccountNo())
                .eq("group_id", groupCodeMappingDO.getGroupId()).set("del", 1));
    }


    @Override
    public Map<String, Object> pageShortLinkByGroupId(Integer page, Integer size, Long accountNo, Long groupId) {
        // 初始化分页信息
        Page<GroupCodeMappingDO> pageInfo = new Page<>(page, size);
        // 执行分页查询
        Page<GroupCodeMappingDO> groupCodeMappingPageResult = groupCodeMappingMapper.selectPage(pageInfo,
                new LambdaQueryWrapper<GroupCodeMappingDO>().eq(GroupCodeMappingDO::getAccountNo, accountNo)
                        .eq(GroupCodeMappingDO::getGroupId, groupId).eq(GroupCodeMappingDO::getDel, 0));
        // 构建返回的分页数据Map
        Map<String, Object> pageMap = new HashMap<>(3);
        // 添加总记录数到Map
        pageMap.put("total_record", groupCodeMappingPageResult.getTotal());
        // 添加总页数到Map
        pageMap.put("total_page", groupCodeMappingPageResult.getPages());
        // 添加当前数据到Map，数据经过beanProcess方法处理转vo
        pageMap.put("current_data", groupCodeMappingPageResult.getRecords()
                .stream().map(this::beanProcess).collect(Collectors.toList()));
        return pageMap;
    }


    @Override
    public int updateGroupCodeMappingState(Long accountNo, Long groupId, String shortLinkCode, ShortLinkStateEnum shortLinkStateEnum) {
        return groupCodeMappingMapper.update(null, new UpdateWrapper<GroupCodeMappingDO>()
                .eq("code", shortLinkCode).eq("account_no", accountNo)
                .eq("group_id", groupId).set("state", shortLinkStateEnum.name()));
    }


    @Override
    public GroupCodeMappingDO findByCodeAndGroupId(String shortLinkCode, Long id, Long accountNo) {

        return groupCodeMappingMapper.selectOne(new QueryWrapper<GroupCodeMappingDO>()
                .eq("code", shortLinkCode)
                .eq("account_no", accountNo)
                .eq("del", 0)
                .eq("group_id", id));
    }

    /**
     * 对象转换
     *
     * @param groupCodeMappingDO do
     * @return vo
     */
    private GroupCodeMappingVO beanProcess(GroupCodeMappingDO groupCodeMappingDO) {
        GroupCodeMappingVO groupCodeMappingVO = new GroupCodeMappingVO();
        BeanUtils.copyProperties(groupCodeMappingDO, groupCodeMappingVO);

        return groupCodeMappingVO;
    }

    @Override
    public int update(GroupCodeMappingDO groupCodeMappingDO) {

        return groupCodeMappingMapper.update(null, new UpdateWrapper<GroupCodeMappingDO>()
                .eq("id", groupCodeMappingDO.getId())
                .eq("account_no", groupCodeMappingDO.getAccountNo())
                .eq("group_id", groupCodeMappingDO.getGroupId())
                .eq("del", 0)
                .eq("code", groupCodeMappingDO.getCode())

                .set("title", groupCodeMappingDO.getTitle())
                .set("domain", groupCodeMappingDO.getDomain())
        );
    }
}
