package com.xyj.gulimall.seckill.scheduled;

import com.xyj.gulimall.seckill.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @Author jie
 * @Date 2023/8/7 19:52
 */

/**
 * 秒杀商品的定时上架
 * 每天晚上3点 进行秒杀上架
 * 当天00:00:00 - 23:59:59
 * 明天00:00:00 - 23:59:59
 * 后天00:00:00 - 23:59:59
 */
@Slf4j
@Service
public class SecKillSkuScheduled {

    @Autowired
    SeckillService seckillService;

    @Autowired
    RedissonClient redissonClient;

    private final static String UPLOAD_LOCK = "seckill:upload:lock";

    /**
     * 秒 分 时 日 月 周
     */
    @Async
    @Scheduled(cron = "0 0 0 * * ?")
    public void uploadSecKillSkuLatest3Days(){
        RLock lock = redissonClient.getLock(UPLOAD_LOCK); // 分布式锁
        lock.lock(10, TimeUnit.SECONDS); //枷锁
        try {
            seckillService.uploadSecKillSkuLatest3Days();
        }catch (Exception ignored){
        }finally {
            lock.unlock(); //解锁
        }
    }
}
