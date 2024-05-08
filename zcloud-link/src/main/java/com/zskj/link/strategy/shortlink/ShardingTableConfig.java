package com.zskj.link.strategy.shortlink;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Xinxuan Zhuo
 * @version 2024/4/30
 * <p>
 * 随机生成短链表位
 * TODO 1.后续必须放到nacos可以动态配置  2.并且可以有权重的实现  3.使用更多位数混合防止被猜测
 * </p>
 */
@SuppressWarnings("ALL")
public class ShardingTableConfig {

    /**
     * 存储数据表位置编号
     */
    private static final List<String> TABLE_SUFFIX_LIST = new ArrayList<>();


    //配置启用那些表的后缀
    static {
        TABLE_SUFFIX_LIST.add("0");
        TABLE_SUFFIX_LIST.add("a");
    }


    /**
     * 根据短链的code的hashCode获取后缀
     * @param code 短链码
     * @return 表位
     */
    public static String getRandomTableSuffix(String code){
        int hashCode = code.hashCode();
        int index = Math.abs(hashCode) % TABLE_SUFFIX_LIST.size();
        return TABLE_SUFFIX_LIST.get(index);
    }
}
