package com.zskj.common.constant;

/**
 * @author Xinxuan Zhuo
 * @version 2024/4/24
 * <p>
 * redis-key常量
 * </p>
 */

public interface RedisKeyConstant {

    /**
     * 验证码缓存key，第一个是类型,第二个是唯一标识比如手机号或者邮箱
     */
     String CHECK_CODE_KEY = "code:%s:%s";

    /**
     * 提交订单令牌的缓存key
     */
    String SUBMIT_ORDER_TOKEN_KEY = "order:submit:%s:%s";


}
