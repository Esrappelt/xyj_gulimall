package com.xyj.gulimal.gulimallsearch.thread;

import java.util.concurrent.*;

/**
 * @Author jie
 * @Date 2023/7/29 8:47
 */
public class CompletableFutureTest {
    static final ThreadPoolExecutor executor = MyExecutor.getExecutor();
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        test3();
    }
    public void test2() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> supplyAsync = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName());
            int i = 10 / 0;
            System.out.println("结束线程");
            return i;
        }, executor).whenComplete((res, e)->{
            System.out.println("结果是" + res);
        }).exceptionally((e)->{
            System.out.println(e.getMessage());
            return 10;
        });
        Integer integer = supplyAsync.get();
        System.out.println(integer);
    }
    public static void test1() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> handle = CompletableFuture.supplyAsync(() -> 10 / 2, executor).handle((res, thr) -> {
            if (res != null) {
                return res * 2;
            }
            if (thr != null) {
                return 0;
            }
            return 0;
        });
        Integer integer = handle.get();
        System.out.println(integer);
    }
    public static void test3() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> handle = CompletableFuture.supplyAsync(() -> 10 / 2, executor).thenApplyAsync((res)-> res * 2,executor);
        Integer integer = handle.get();
        System.out.println(integer);
    }
}
class MyExecutor{
    private static final int corePoolSize = 7;// 核心线程数
    private static final int maximumPoolSize = 20;//最大线程数
    private static final long keepAliveTime = 0;
    private static final TimeUnit unit = TimeUnit.SECONDS;
    private static final BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(50);
    private static final ThreadFactory threadFactory = Executors.defaultThreadFactory();
    private static final RejectedExecutionHandler handler = new ThreadPoolExecutor.CallerRunsPolicy();
    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(
            corePoolSize,
            maximumPoolSize,
            keepAliveTime,
            unit,
            workQueue,
            threadFactory,
            handler
    );
    static ThreadPoolExecutor getExecutor(){
        return executor;
    }
}