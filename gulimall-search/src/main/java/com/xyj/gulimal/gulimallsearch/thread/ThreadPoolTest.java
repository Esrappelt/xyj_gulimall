package com.xyj.gulimal.gulimallsearch.thread;

import java.util.concurrent.*;

/**
 * @Author jie
 * @Date 2023/7/27 15:47
 */
public class ThreadPoolTest {
    public static void main(String[] args) {
        int corePoolSize = 7;// 核心线程数
        int maximumPoolSize = 20;//最大线程数
        long keepAliveTime = 0;
        TimeUnit unit = TimeUnit.SECONDS;
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(50);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        RejectedExecutionHandler handler = new ThreadPoolExecutor.CallerRunsPolicy();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                unit,
                workQueue,
                threadFactory,
                handler
        );
    }




}
