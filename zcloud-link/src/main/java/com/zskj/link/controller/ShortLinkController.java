package com.zskj.link.controller;


import com.zskj.common.util.JsonData;
import com.zskj.link.controller.request.ShortLinkAddRequest;
import com.zskj.link.controller.request.ShortLinkDelRequest;
import com.zskj.link.controller.request.ShortLinkPageRequest;
import com.zskj.link.controller.request.ShortLinkUpdateRequest;
import com.zskj.link.service.ShortLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Xinxuan Zhuo
 * @since 2024-04-29
 */
@RestController
@RequestMapping("/api/link/v1")
public class ShortLinkController {
    @Autowired
    private ShortLinkService shortLinkService;


    /**
     * 新增短链
     * @param request req
     * @return jsonData
     */
    @PostMapping("/add")
    public JsonData createShortLink(@RequestBody ShortLinkAddRequest request){
        return shortLinkService.createShortLink(request);
    }


    /**
     * 分页查找短链 B端
     * @param request req
     * @return jsonData
     */
    @RequestMapping("/page")
    public JsonData pageByGroupId(@RequestBody ShortLinkPageRequest request){
        Map<String,Object> result = shortLinkService.pageByGroupId(request);
        return JsonData.buildSuccess(result);
    }

    /**
     * 删除短链
     * @param request req
     * @return jsonData
     */
    @PostMapping("/del")
    public JsonData delShortLink(@RequestBody ShortLinkDelRequest request){
        return shortLinkService.delShortLink(request);
    }


    /**
     * 更新短链
     * @param request req
     * @return jsonData
     */
    @PostMapping("/update")
    public JsonData updateShortLink(@RequestBody ShortLinkUpdateRequest request){
        return shortLinkService.updateShortLink(request);
    }
}

