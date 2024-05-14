package com.zskj.shop.service;

import com.zskj.shop.vo.ProductVO;

import java.util.List;

/**
 * @author Xinxuan Zhuo
 * @version 2024/5/14
 * <p>
 *
 * </p>
 */

public interface ProductService{

    /**
     * 商品列表
     * @return list
     */
    List<ProductVO> list();

    /**
     * 商品详情
     * @param productId 商品id
     * @return vo
     */
    ProductVO findDetailById(long productId);
}
