package com.zskj.shop.manager;

import com.zskj.shop.model.ProductDO;

import java.util.List;

/**
 * @author Xinxuan Zhuo
 * @version 2024/5/14
 * <p>
 *
 * </p>
 */

public interface ProductManager {

    /**
     * 商品列表
     * @return list
     */
    List<ProductDO> list();

    /**
     * 商品详情
     * @param productId 商品id
     * @return do
     */
    ProductDO findDetailById(long productId);
}
