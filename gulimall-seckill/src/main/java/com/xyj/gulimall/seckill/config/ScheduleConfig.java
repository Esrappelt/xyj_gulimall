package com.xyj.gulimall.seckill.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @Author jie
 * @Date 2023/8/7 19:52
 */

/**
 * 开启异步任务功能
 * @EnableAsync
 * 使用时，添加@Async注解即可
 */
@EnableAsync
@EnableScheduling
@Configuration
public class ScheduleConfig {
}
