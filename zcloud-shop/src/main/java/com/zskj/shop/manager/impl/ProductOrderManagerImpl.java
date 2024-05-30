package com.zskj.shop.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zskj.shop.manager.ProductOrderManager;
import com.zskj.shop.mapper.ProductOrderMapper;
import com.zskj.shop.model.ProductOrderDO;
import com.zskj.shop.vo.ProductOrderVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Xinxuan Zhuo
 * @version 2024/5/14
 * <p>
 *
 * </p>
 */
@SuppressWarnings("ALL")
@Service
public class ProductOrderManagerImpl implements ProductOrderManager {

    @Autowired
    private ProductOrderMapper productOrderMapper;

    @Override
    public int add(ProductOrderDO productOrderDO) {
        return productOrderMapper.insert(productOrderDO);
    }

    @Override
    public ProductOrderDO findByOutTradeNoAndAccountNo(String outTradeNo, Long accountNo) {
        ProductOrderDO productOrderDO = productOrderMapper.selectOne(new LambdaQueryWrapper<ProductOrderDO>()
                .eq(ProductOrderDO::getOutTradeNo, outTradeNo)
                .eq(ProductOrderDO::getAccountNo, accountNo)
                .eq(ProductOrderDO::getDel, 0)
                .select(ProductOrderDO::getId,
                        ProductOrderDO::getState,
                        ProductOrderDO::getBuyNum,
                        ProductOrderDO::getOutTradeNo,
                        ProductOrderDO::getProductSnapshot));
        return productOrderDO;
    }


    @Override
    public int updateOrderPayState(String outTradeNo, Long accountNo, String newState, String oldState) {
        int rows = productOrderMapper.update(null, new UpdateWrapper<ProductOrderDO>()
                .eq("out_trade_no", outTradeNo)
                .eq("account_no", accountNo)
                .eq("state", oldState)
                .eq("del", 0)

                .set("state", newState));
        return rows;
    }

    @Override
    public Map<String, Object> page(int page, int size, Long accountNo, String state) {
        // 初始化分页信息
        Page<ProductOrderDO> pageInfo = new Page<>(page, size);

        // 根据是否提供了订单状态来构建查询条件
        Page<ProductOrderDO> productOrderPage = productOrderMapper.selectPage(pageInfo, new LambdaQueryWrapper<ProductOrderDO>()
                .eq(ProductOrderDO::getAccountNo, accountNo)
                .eq(ProductOrderDO::getDel, 0)
                .eq(StringUtils.isNotBlank(state), ProductOrderDO::getState, state)
        );
        if (CollectionUtils.isEmpty(productOrderPage.getRecords())) {
            return Collections.emptyMap();
        }
        // 将查询结果转换为VO对象列表
        List<ProductOrderDO> orderDOIPageRecords = productOrderPage.getRecords();
        List<ProductOrderVO> productOrderVOList = orderDOIPageRecords.stream().map(obj -> {
            ProductOrderVO productOrderVO = new ProductOrderVO();
            BeanUtils.copyProperties(obj, productOrderVO);
            return productOrderVO;
        }).collect(Collectors.toList());

        // 构建并返回包含分页信息和数据列表的Map
        Map<String, Object> pageMap = new HashMap<>(3);
        pageMap.put("total_record", productOrderPage.getTotal());
        pageMap.put("total_page", productOrderPage.getPages());
        pageMap.put("current_data", productOrderVOList);
        return pageMap;
    }


    @Override
    public int del(Long productOrderId, Long accountNo) {
        int rows = productOrderMapper.update(null, new UpdateWrapper<ProductOrderDO>()
                .eq("id", productOrderId)
                .eq("account_no", accountNo)
                .set("del", 1));
        return rows;
    }
}
