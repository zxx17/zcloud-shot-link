package com.zskj.shop.annotation;

import java.lang.annotation.*;

/**
 * @author Xinxuan Zhuo
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RepeatSubmit {
    /**
     * 防重提交，支持两种，一个是方法参数，一个是令牌
     */
    enum Type {
        /**
         * token
         */
        TOKEN,
        /**
         * param
         */
        PARAM
    }

    /**
     * 默认防重提交，是方法参数
     * @return enum
     */
    Type limitType() default Type.PARAM;

    /**
     * 锁定时间
     * @return seconds
     */
    long lockTime() default 5;

}
