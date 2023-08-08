package com.xyj.gulimall.seckill.scheduled;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @Author jie
 * @Date 2023/8/7 19:33
 */
@Slf4j
@Component
@EnableScheduling
public class TestSchedule {
    /**
     * 秒 分 时 日 月 周
     * 默认是阻塞的，要解决不阻塞方法有3种：
     * 第一种方式是使用异步方式Completable+executor 提交到线程池执行
     * 第二种方式使用定时任务线程池
     * 第三种方式是：开启异步任务
     */
//    @Scheduled(cron = "* * * ? * 1")
//    @Async
    public void hello() throws InterruptedException {
        log.info("hello-schedule-task");
        Thread.sleep(2000);
    }
}
