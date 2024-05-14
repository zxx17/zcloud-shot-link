package com.zskj.shop.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zskj.shop.manager.ProductManager;
import com.zskj.shop.mapper.ProductMapper;
import com.zskj.shop.model.ProductDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Xinxuan Zhuo
 * @version 2024/5/14
 * <p>
 *
 * </p>
 */

@Service
public class ProductManagerImpl implements ProductManager {

    @Autowired
    private ProductMapper productMapper;

    @Override
    public List<ProductDO> list() {
        return productMapper.selectList(null);
    }

    @Override
    public ProductDO findDetailById(long productId) {
        return productMapper.selectOne(new LambdaQueryWrapper<ProductDO>()
                .eq(ProductDO::getId, productId));
    }
}
