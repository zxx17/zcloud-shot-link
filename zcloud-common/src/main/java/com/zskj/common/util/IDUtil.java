package com.zskj.common.util;

import org.apache.shardingsphere.core.strategy.keygen.SnowflakeShardingKeyGenerator;

/**
 * @author Xinxuan Zhuo
 * @version 2024/4/27
 * <p>
 * 雪花算法id工具类
 * </p>
 */

public class IDUtil {
    private static final SnowflakeShardingKeyGenerator SNOWFLAKE_SHARDING_KEY_GENERATOR = new SnowflakeShardingKeyGenerator();


    /**
     * 生成雪花算法id
     */
    public static Comparable<?> geneSnowFlakeId() {
        return SNOWFLAKE_SHARDING_KEY_GENERATOR.generateKey();
    }

}
