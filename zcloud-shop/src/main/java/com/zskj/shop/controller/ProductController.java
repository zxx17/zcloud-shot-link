package com.zskj.shop.controller;


import com.zskj.common.util.JsonData;
import com.zskj.shop.service.ProductService;
import com.zskj.shop.vo.ProductVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Xinxuan Zhuo
 * @since 2024-05-14
 */
@RestController
@RequestMapping("/api/product/v1")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * 查看商品列表
     * @return jsonData
     */
    @GetMapping("list")
    public JsonData list(){
        List<ProductVO> list = productService.list();
        return JsonData.buildSuccess(list);
    }

    /**
     * 查看商品详情
     * @param productId 商品id
     */
    @GetMapping("detail/{product_id}")
    public JsonData detail(@PathVariable("product_id") long productId){
        ProductVO productVO = productService.findDetailById(productId);
        return JsonData.buildSuccess(productVO);
    }

}

