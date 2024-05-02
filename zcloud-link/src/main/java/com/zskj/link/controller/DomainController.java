package com.zskj.link.controller;

import com.zskj.common.util.JsonData;
import com.zskj.link.service.DomainService;
import com.zskj.link.vo.DomainVO;
import groovy.transform.AutoClone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Xinxuan Zhuo
 * @version 2024/5/2
 * <p>
 *
 * </p>
 */
@RestController
@RequestMapping("/api/domain/v1")
public class DomainController {

    @Autowired
    private DomainService domainService;

    /**
     * 列举全部可用域名列表
     * @return jsonData
     */
    @GetMapping("/list")
    public JsonData listAll(){
        List<DomainVO> list = domainService.listAll();
        return JsonData.buildSuccess(list);
    }


}
