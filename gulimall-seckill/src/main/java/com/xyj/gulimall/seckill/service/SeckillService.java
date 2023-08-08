package com.xyj.gulimall.seckill.service;

import com.xyj.gulimall.seckill.to.SeckillSkuRedisTo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author jie
 * @Date 2023/8/7 19:58
 */
public interface SeckillService {
    void uploadSecKillSkuLatest3Days();

    List<SeckillSkuRedisTo> getCurrentSeckillSkus();

    SeckillSkuRedisTo getSkuSeckillInfo(Long skuId);

    String kill(String killId, String key, Integer num);
}
