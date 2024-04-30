package com.zskj.link.strategy.sharding;

import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;

import java.util.Collection;

/**
 * @author Xinxuan Zhuo
 * @version 2024/4/30
 * <p>
 * 分库策略
 * </p>
 */
@Slf4j
@SuppressWarnings("ALL")
public class CustomDBPreciseShardingAlgorithm implements PreciseShardingAlgorithm<String> {


    /**
     * @param availableTargetNames 数据源集合
     *                             在分库时值为所有分片库的集合 databaseNames
     *                             分表时为对应分片库中所有分片表的集合 tablesNames
     * @param shardingValue        分片属性，包括
     *                             logicTableName 为逻辑表，
     *                             columnName 分片健（字段），
     *                             value 为从 SQL 中解析出的分片健的值
     * @return
     */
    @Override
    public String doSharding(Collection<String> collection, PreciseShardingValue<String> preciseShardingValue) {
        // 获取短链码第一位 即库位
        String codePrefix = preciseShardingValue.getValue().substring(0, 1);

        // 获取库的最后一位，匹配短链码库位进行路由
        for (String dbName : collection) {
            // 获取库名的最后一位，真实配置的ds
            String targetNameSuffix = dbName.substring(dbName.length() - 1);

            //如果一致则返回
            if (codePrefix.equals(targetNameSuffix)) {
                return dbName;
            }
        }
        log.error("找不到数据库，对应的短链码库位：{}", codePrefix);
        throw new RuntimeException("找不到数据库");
    }
}
