package com.xyj.gulimall.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author jie
 * @Date 2023/6/29 13:41
 */
@Configuration
public class MyRedissonConfig {
    private static final String ADDRESS = "192.168.28.101:6379";
    /**
     * 通过RedisClient对象
     * @return config
     */
    @Bean(destroyMethod = "shutdown")
    public RedissonClient redisson() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://" + ADDRESS);
        return Redisson.create(config);
    }
}
