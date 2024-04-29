package com.zskj.link.service;

import com.zskj.link.controller.request.LinkGroupAddRequest;
import com.zskj.link.controller.request.LinkGroupUpdateRequest;
import com.zskj.link.vo.LinkGroupVO;

import java.util.List;

/**
 * @author Xinxuan Zhuo
 * @version 2024/4/29
 * <p>
 * 短链分组service
 * </p>
 */

public interface LinkGroupService {

    /**
     * 添加分组
     * @param request request
     * @return jsonData
     */
    int add(LinkGroupAddRequest request);

    /**
     * 根据id删除分组
     * @param groupId 分组id
     * @return jsonData
     */
    int del(Long groupId);

    /**
     * 根据id找详情
     * @param groupId 分组id
     * @return jsonData
     */
    LinkGroupVO detail(Long groupId);

    /**
     * 列出用户全部分组
     * @return List<LinkGroupVO>
     */
    List<LinkGroupVO> listAllGroup();

    /**
     * 更新组名
     * @param request request
     * @return 影响行数
     */
    int updateById(LinkGroupUpdateRequest request);
}
