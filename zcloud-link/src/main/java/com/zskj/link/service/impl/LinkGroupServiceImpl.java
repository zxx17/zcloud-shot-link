package com.zskj.link.service.impl;

import com.zskj.common.interceptor.LoginInterceptor;
import com.zskj.link.controller.request.LinkGroupAddRequest;
import com.zskj.link.controller.request.LinkGroupUpdateRequest;
import com.zskj.link.manager.LinkGroupManager;
import com.zskj.link.model.LinkGroupDO;
import com.zskj.link.service.LinkGroupService;
import com.zskj.link.vo.LinkGroupVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Xinxuan Zhuo
 * @version 2024/4/29
 * <p>
 * 短链分组service
 * </p>
 */


@Slf4j
@Service
public class LinkGroupServiceImpl implements LinkGroupService {


    @Autowired
    private LinkGroupManager linkGroupManager;

    /**
     * 添加分组
     *
     * @param request request
     * @return jsonData
     */
    @Override
    public int add(LinkGroupAddRequest request) {
        // 获取当前登录的用户账号
        Long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();
        // 封装
        LinkGroupDO linkGroupDO = new LinkGroupDO();
        linkGroupDO.setTitle(request.getTitle());
        linkGroupDO.setAccountNo(accountNo);
        return linkGroupManager.add(linkGroupDO);
    }

    /**
     * 根据id删除分组
     *
     * @param groupId 分组id
     * @return jsonData
     */
    @Override
    public int del(Long groupId) {
        // 获取当前登录的用户账号
        Long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();
        return linkGroupManager.del(groupId, accountNo);
    }

    /**
     * 根据id找详情
     *
     * @param groupId 分组id
     * @return jsonData
     */
    @Override
    public LinkGroupVO detail(Long groupId) {
        Long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();
        LinkGroupDO linkGroupDO = linkGroupManager.detail(groupId, accountNo);
        LinkGroupVO linkGroupVO = new LinkGroupVO();
        BeanUtils.copyProperties(linkGroupDO, linkGroupVO);
        return linkGroupVO;
    }

    /**
     * 列出用户全部分组
     *
     * @return List<LinkGroupVO>
     */
    @Override
    public List<LinkGroupVO> listAllGroup() {
        Long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();
        List<LinkGroupDO> linkGroupDOList = linkGroupManager.listAllGroup(accountNo);

        return linkGroupDOList.stream().map(obj -> {
            LinkGroupVO linkGroupVO = new LinkGroupVO();
            BeanUtils.copyProperties(obj, linkGroupVO);
            return linkGroupVO;
        }).collect(Collectors.toList());
    }


    /**
     * 更新组名
     *
     * @param request request
     * @return 影响行数
     */
    @Override
    public int updateById(LinkGroupUpdateRequest request) {
        Long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();
        LinkGroupDO linkGroupDO = new LinkGroupDO();
        linkGroupDO.setTitle(request.getTitle());
        linkGroupDO.setId(request.getId());
        linkGroupDO.setAccountNo(accountNo);
        return linkGroupManager.updateById(linkGroupDO);
    }
}
