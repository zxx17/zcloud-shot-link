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

    private static final Random RANDOM = new Random();

    //配置启用那些表的后缀
    static {
        TABLE_SUFFIX_LIST.add("0");
        TABLE_SUFFIX_LIST.add("a");
    }


    /**
     * 获取随机的后缀
     * @return 表位
     */
    public static String getRandomTableSuffix(){
        int index = RANDOM.nextInt(TABLE_SUFFIX_LIST.size());
        return TABLE_SUFFIX_LIST.get(index);
    }
}
