package com.zskj.common.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Xinxuan Zhuo
 * @version 2024/5/16
 * <p>
 * redisson配置类
 * </p>
 */

@Configuration
@EnableConfigurationProperties(RedisProperties.class)
public class RedissonConfiguration {

    /**
     * 配置分布式锁的redisson(简单配置)
     * TODO 集群和哨兵的配置参照在线教育项目
     */
    @Bean
    public RedissonClient redissonClient(RedisProperties properties) {
        Config config = new Config();
        //单机方式
        config.useSingleServer()
                .setDatabase(0)
                .setPassword(properties.getPassword())
                .setAddress(String.format("redis://%s:%d", properties.getHost(), properties.getPort()));
        //集群
        //config.useClusterServers().addNodeAddress("redis://192.31.21.1:6379","redis://192.31.21.2:6379")
        return Redisson.create(config);
    }

}
