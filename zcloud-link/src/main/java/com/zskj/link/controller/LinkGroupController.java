package com.zskj.link.controller;


import com.zskj.common.enums.BizCodeEnum;
import com.zskj.common.util.JsonData;
import com.zskj.link.controller.request.LinkGroupAddRequest;
import com.zskj.link.controller.request.LinkGroupUpdateRequest;
import com.zskj.link.service.LinkGroupService;
import com.zskj.link.vo.LinkGroupVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Xinxuan Zhuo
 * @since 2024-04-29
 */
@RestController
@RequestMapping("/api/group/v1")
public class LinkGroupController {

    @Autowired
    private LinkGroupService linkGroupService;

    /**
     * 添加分组
     * @param request request
     * @return jsonData
     */
    @PostMapping("/add")
    public JsonData add(@RequestBody @Validated LinkGroupAddRequest request){
        int rows = linkGroupService.add(request);
        return rows == 1 ? JsonData.buildSuccess():JsonData.buildResult(BizCodeEnum.GROUP_ADD_FAIL);
    }

    /**
     * 根据id删除分组
     * @param groupId 分组id
     * @return jsonData
     */
    @DeleteMapping("/del/{group_id}")
    public JsonData del(@PathVariable("group_id") Long groupId){

        int rows = linkGroupService.del(groupId);
        return rows == 1 ? JsonData.buildSuccess():JsonData.buildResult(BizCodeEnum.GROUP_NOT_EXIST);

    }

    /**
     * 根据id找详情
     * @param groupId 分组id
     * @return jsonData
     */
    @GetMapping("/detail/{group_id}")
    public JsonData detail(@PathVariable("group_id") Long groupId){
        LinkGroupVO linkGroupVO = linkGroupService.detail(groupId);
        return JsonData.buildSuccess(linkGroupVO);

    }


    /**
     * 列出用户全部分组
     * @return jsonData
     */
    @GetMapping("/list")
    public JsonData findUserAllLinkGroup(){
        List<LinkGroupVO> list = linkGroupService.listAllGroup();
        return JsonData.buildSuccess(list);
    }



    /**
     * 更新分组信息
     * @return JsonData
     */
    @PutMapping("/update")
    public JsonData update(@RequestBody LinkGroupUpdateRequest request){
        int rows = linkGroupService.updateById(request);
        return rows == 1 ? JsonData.buildSuccess():JsonData.buildResult(BizCodeEnum.GROUP_OPER_FAIL);

    }
}

