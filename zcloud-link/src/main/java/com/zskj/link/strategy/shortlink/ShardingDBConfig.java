package com.zskj.link.strategy.shortlink;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Xinxuan Zhuo
 * @version 2024/4/30
 * <p>
 * 随机生成短链库位
 * TODO 1.后续必须放到nacos可以动态配置  2.并且可以有权重的实现  3.使用更多位数混合防止被猜测
 * </p>
 */

@Slf4j
@SuppressWarnings("ALL")
public class ShardingDBConfig {


    /**
     * 存储数据库位置编号
     */
    private static final List<String> DB_PREFIX_LIST = new ArrayList<>();


    //配置启用那些库的前缀
    static {
        DB_PREFIX_LIST.add("0");
        DB_PREFIX_LIST.add("1");
        DB_PREFIX_LIST.add("a");
    }


    /**
     * 获取随机的前缀
     * @return 库位
     */
    public static String getRandomDBPrefix(String code){
        int hashCode = code.hashCode();
        int index = Math.abs(hashCode) % DB_PREFIX_LIST.size();
        return DB_PREFIX_LIST.get(index);
    }
}
