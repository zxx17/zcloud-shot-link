package com.zskj.shop.manager;

import com.zskj.shop.model.ProductOrderDO;

import java.util.Map;

/**
 * @author Xinxuan Zhuo
 * @version 2024/5/14
 * <p>
 *
 * </p>
 */

public interface ProductOrderManager {
    /***
     * 新增
     * @param productOrderDO do
     * @return int
     */
    int add(ProductOrderDO productOrderDO);



    /**
     * 通过订单号和账号查询
     * @param outTradeNo 订单号
     * @param accountNo 账号
     * @return do
     */
    ProductOrderDO findByOutTradeNoAndAccountNo(String outTradeNo,Long accountNo);


    /**
     * 更新订单状态
     * @param outTradeNo 订单号
     * @param accountNo 账号
     * @param newState 状态
     * @param oldState 状态
     * @return int
     */
    int updateOrderPayState(String outTradeNo,Long accountNo,String newState,String oldState);


    /**
     * 分页查看订单列表
     * @param page page
     * @param size size
     * @param accountNo accountNo
     * @param state status
     * @return map
     */
    Map<String,Object> page(int page, int size, Long accountNo, String state);


    /**
     * 删除
     * @param productOrderId 订单id
     * @param accountNo 账号
     * @return int
     */
    int del(Long productOrderId,Long accountNo);
}
