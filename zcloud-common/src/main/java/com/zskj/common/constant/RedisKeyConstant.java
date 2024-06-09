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
     * 提交订单令牌的缓存key  accountNo  token令牌
     */
    public static final String SUBMIT_ORDER_TOKEN_KEY = "order:submit:%s:%s";

    /**
     * 用户当天剩余的可用流量包次数  accountNo
     */
    public static final String DAY_TOTAL_TRAFFIC = "lock:traffic:day_total:%s";


}
