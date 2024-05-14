package com.zskj.shop.service.impl;

import com.zskj.shop.manager.ProductManager;
import com.zskj.shop.model.ProductDO;
import com.zskj.shop.service.ProductService;
import com.zskj.shop.vo.ProductVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Xinxuan Zhuo
 * @version 2024/5/14
 * <p>
 *
 * </p>
 */

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductManager productManager;


    @Override
    public List<ProductVO> list() {
        List<ProductDO> list = productManager.list();
        List<ProductVO> collect = list.stream().map(this::beanProcess).collect(Collectors.toList());
        return collect;
    }

    @Override
    public ProductVO findDetailById(long productId) {
        ProductDO productDO = productManager.findDetailById(productId);
        return beanProcess(productDO);
    }


    private ProductVO beanProcess(ProductDO productDO) {
        ProductVO productVO = new ProductVO();
        BeanUtils.copyProperties(productDO, productVO);
        return productVO;
    }
}
