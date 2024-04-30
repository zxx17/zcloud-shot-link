package com.zskj.link.strategy.sharding;

import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;

import java.util.Collection;

/**
 * @author Xinxuan Zhuo
 * @version 2024/4/30
 * <p>
 * 分表策略
 * </p>
 */
@Slf4j
@SuppressWarnings("ALL")
public class CustomTablePreciseShardingAlgorithm implements PreciseShardingAlgorithm<String> {

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
    public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<String> preciseShardingValue) {
        // 拿到短链码的表位
        String shotLinkCode = preciseShardingValue.getValue();
        String shotLinkSuffix = shotLinkCode.substring(shotLinkCode.length() - 1);

        // 获取逻辑表
        String targetName = availableTargetNames.iterator().next();

        //拼接Actual table
        return targetName + "_" + shotLinkSuffix;
    }
}
