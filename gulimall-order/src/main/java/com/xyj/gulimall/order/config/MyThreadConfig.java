package com.xyj.gulimall.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author jie
 * @Date 2023/7/29 15:55
 */
@Configuration
public class MyThreadConfig {
    @Bean
    public ThreadPoolExecutor threadPoolExecutor(ThreadPoolConfigProperties pool){
        Integer coreSize = pool.getCoreSize();
        Integer maxSize = pool.getMaxSize();
        Integer keepAliveTime = pool.getKeepAliveTime();
        return new ThreadPoolExecutor(coreSize, maxSize, keepAliveTime,
                TimeUnit.SECONDS, new LinkedBlockingQueue<>(100000),
                Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
    }
}
