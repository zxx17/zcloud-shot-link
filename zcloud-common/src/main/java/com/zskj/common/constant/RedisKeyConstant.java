package com.zskj.common.constant;

/**
 * @author Xinxuan Zhuo
 * @version 2024/4/24
 * <p>
 * redis-key常量
 * </p>
 */

public final class RedisKeyConstant {

    /**
     * 验证码缓存key，第一个是类型,第二个是唯一标识比如手机号或者邮箱
     */
    public static final String CHECK_CODE_KEY = "code:%s:%s";

    /**
     * 提交订单令牌的缓存key
     */
    public static final String SUBMIT_ORDER_TOKEN_KEY = "order:submit:%s:%s";


}
