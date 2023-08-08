package com.xyj.gulimall.seckill.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.xyj.common.to.SeckillOrderTo;
import com.xyj.common.utils.R;
import com.xyj.common.vo.MemberResponseVo;
import com.xyj.gulimall.seckill.feign.CouponFeignService;
import com.xyj.gulimall.seckill.feign.ProductFeignService;
import com.xyj.gulimall.seckill.service.SeckillService;
import com.xyj.gulimall.seckill.to.SeckillSkuRedisTo;
import com.xyj.gulimall.seckill.vo.SeckillSessionWithSkus;
import com.xyj.gulimall.seckill.vo.SkuInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.xyj.gulimall.seckill.interceptor.LoginUserInterceptor.loginUser;

/**
 * @Author jie
 * @Date 2023/8/7 19:58
 */
@Slf4j
@Service
public class SeckillServiceImpl implements SeckillService {

    private final static String SESSIONS_CACHE_PREFIX = "seckill:sessions:";
    private final static String SKUKILL_CACHE_PREFIX = "seckill:skus:";
    private final static String SKU_STOCK_SEMAPHORE = "seckill:stock:";
    private final static String USER_PURCHASE_PLACE = "user:purchase:placeholder:";


    @Autowired
    CouponFeignService couponFeignService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    RedissonClient redissonClient;

    @Override
    public void uploadSecKillSkuLatest3Days() {
        //扫描最近3天需要参与秒杀的活动
        R r = couponFeignService.getLatest3DaySession();
        if (r.getCode() == 0) {
            List<SeckillSessionWithSkus> data = r.getData(new TypeReference<List<SeckillSessionWithSkus>>() {
            });
            // 放入redis
            // 1.保存活动信息
            saveSessionInfo(data);
            // 2.缓存活动的关联的商品信息
            saveSessionSkuInfo(data);
        }
    }

    @Override
    public List<SeckillSkuRedisTo> getCurrentSeckillSkus() {
        // 1.确定当前时间属于哪个秒杀场次
        long currentTime = new Date().getTime();
        Set<String> keys = stringRedisTemplate.keys(SESSIONS_CACHE_PREFIX + "*");
        if (keys != null) {
            for (String key : keys) {
                String replaceKey = key.replace(SESSIONS_CACHE_PREFIX, "");
                String[] s = replaceKey.split("_");
                long start = Long.parseLong(s[0]);
                long end = Long.parseLong(s[1]);
                if (currentTime >= start && currentTime <= end) {
                    List<String> keysList = stringRedisTemplate.opsForList().range(key, 0, -1);
                    BoundHashOperations<String, String, String> boundHashOps = stringRedisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
                    if (keysList != null) {
                        List<String> list = boundHashOps.multiGet(keysList);
                        if (list != null && list.size() > 0) {
                            return list.stream().map(item -> JSON.parseObject(item, SeckillSkuRedisTo.class)).collect(Collectors.toList());
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * 根据skuId查询商品是否参加秒杀活动
     *
     * @param skuId
     * @return
     */
    @Override
    public SeckillSkuRedisTo getSkuSeckillInfo(Long skuId) {

        //1、找到所有需要秒杀的商品的key信息---seckill:skus
        BoundHashOperations<String, String, String> hashOps = stringRedisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);

        //拿到所有的key
        Set<String> keys = hashOps.keys();
        if (keys != null && keys.size() > 0) {
            //4-45 正则表达式进行匹配
            String reg = "\\d_" + skuId;
            for (String key : keys) {
                //如果匹配上了
                if (Pattern.matches(reg, key)) {
                    //从Redis中取出数据来
                    String redisValue = hashOps.get(key);
                    //进行序列化
                    SeckillSkuRedisTo redisTo = JSON.parseObject(redisValue, SeckillSkuRedisTo.class);
                    if (redisTo != null) {
                        //随机码
                        Long currentTime = System.currentTimeMillis();
                        Long startTime = redisTo.getStartTime();
                        Long endTime = redisTo.getEndTime();
                        //如果当前时间大于等于秒杀活动开始时间并且要小于活动结束时间
                        if (currentTime >= startTime && currentTime <= endTime) {
                            return redisTo;
                        } else if (currentTime <= startTime) { //过期的活动场次 不需要返回
                            redisTo.setRandomCode(null);
                            return redisTo;
                        }
                    }
                }
            }
        }
        return null;
    }

    @Autowired
    RabbitTemplate rabbitTemplate;

    private volatile String orderSn = null;

    @Override
    public String kill(String killId, String key, Integer num) {
        boolean ok = canKill(killId, key, num);
        if (!ok) return null;
        // 秒杀成功
        return orderSn;
    }

    /**
     * TODO 秒杀商品上架时，设置过期时间，库存扣减等
     * @param killId
     * @param key
     * @param num
     * @return
     */
    private boolean canKill(String killId, String key, Integer num) {

        long start = System.currentTimeMillis();

        BoundHashOperations<String, String, String> boundHashOps =
                stringRedisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
        String skus = boundHashOps.get(killId);
        //校验商品是否存在
        if (StringUtils.isEmpty(skus)) {
            return false;
        }
        SeckillSkuRedisTo seckillSkuRedisTo = JSON.parseObject(skus, SeckillSkuRedisTo.class);
        Long startTime = seckillSkuRedisTo.getStartTime();
        Long endTime = seckillSkuRedisTo.getEndTime();
        long currentedTime = System.currentTimeMillis();
        // 校验时间是否正确
        if (!(currentedTime >= startTime && currentedTime <= endTime)) {
            return false;
        }
        // 参数校验
        String randomCode = seckillSkuRedisTo.getRandomCode();
        String skuId = seckillSkuRedisTo.getPromotionSessionId() + "_" + seckillSkuRedisTo.getSkuId();
        if (!randomCode.equals(key) || !skuId.equals(killId)) {
            return false;
        }
        // 购买商品数量判断
        BigDecimal num_bigdecimal = new BigDecimal(num);
        BigDecimal seckillLimit = seckillSkuRedisTo.getSeckillLimit();
        if (num_bigdecimal.compareTo(seckillLimit) > 0) {
            return false;
        }
        // 验证这个人是否已经购买过了
        // SETNX
        MemberResponseVo memberResponseVo = loginUser.get();
        String redisKey = USER_PURCHASE_PLACE + memberResponseVo.getId() + "_" + skuId;
        long keyTTL = endTime - currentedTime;
        Boolean okSet = stringRedisTemplate.opsForValue().setIfAbsent(redisKey, num.toString(), keyTTL, TimeUnit.MILLISECONDS);
        // 占位失败 说明这个人已经买过这个商品了
        if (Boolean.FALSE.equals(okSet)) {
            return false;
        }
        RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE + randomCode);
        try {
            boolean tryAcquire = semaphore.tryAcquire(num);
            if (!tryAcquire) return false; //没获取到
            // 放入MQ
            SeckillOrderTo seckillOrderTo = new SeckillOrderTo();
            orderSn = IdWorker.getTimeId();
            seckillOrderTo.setOrderSn(orderSn);
            seckillOrderTo.setNum(num);
            seckillOrderTo.setMemberId(memberResponseVo.getId());
            seckillOrderTo.setSkuId(seckillSkuRedisTo.getSkuId());
            seckillOrderTo.setPromotionSessionId(seckillSkuRedisTo.getPromotionSessionId());
            seckillOrderTo.setSeckillPrice(seckillSkuRedisTo.getSeckillPrice());
            rabbitTemplate.convertAndSend("order-event-exchange", "order.seckill.order", seckillOrderTo);
            long end = System.currentTimeMillis();
            log.info("耗时: {}", end-start);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    private void saveSessionSkuInfo(List<SeckillSessionWithSkus> sessions) {
        if(sessions == null) return;
        sessions.forEach(session -> {
            long startTime = session.getStartTime().getTime();
            long endTime = session.getEndTime().getTime();
            String key = SESSIONS_CACHE_PREFIX + startTime + "_" + endTime;
            Boolean hasKey = stringRedisTemplate.hasKey(key);
            if (Boolean.TRUE.equals(hasKey)) {
                return;
            }
            List<String> skus = session.getRelationSkus().stream().map(i -> i.getPromotionSessionId().toString() + "_" + i.getSkuId().toString()).collect(Collectors.toList());
            stringRedisTemplate.opsForList().leftPushAll(key, skus);
        });
    }

    private void saveSessionInfo(List<SeckillSessionWithSkus> sessions) {
        if(sessions == null) return;
        sessions.forEach(session -> {
            //hash操作
            BoundHashOperations<String, Object, Object> boundHashOps = stringRedisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
            session.getRelationSkus().forEach(seckillSkuVo -> {
                if (Boolean.FALSE.equals(boundHashOps.hasKey(seckillSkuVo.getPromotionSessionId().toString() + "_" + seckillSkuVo.getSkuId().toString()))) {
                    //缓存商品
                    SeckillSkuRedisTo seckillSkuRedisTo = new SeckillSkuRedisTo();
                    // sku秒杀信息
                    BeanUtils.copyProperties(seckillSkuVo, seckillSkuRedisTo);
                    // sku基本信息
                    R skuR = productFeignService.getSkuInfo(seckillSkuVo.getSkuId());
                    if (skuR.getCode() == 0) {
                        SkuInfoVo skuInfo = skuR.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                        });
                        seckillSkuRedisTo.setSkuInfo(skuInfo);
                    }
                    // 秒杀时间
                    seckillSkuRedisTo.setStartTime(session.getStartTime().getTime());
                    seckillSkuRedisTo.setEndTime(session.getEndTime().getTime());
                    // 随机码
                    String token = UUID.randomUUID().toString().replace("-", "");
                    String key = SKU_STOCK_SEMAPHORE + token;
                    seckillSkuRedisTo.setRandomCode(token);
                    String redisTo = JSON.toJSONString(seckillSkuRedisTo);
                    boundHashOps.put(seckillSkuVo.getPromotionSessionId().toString() + "_" + seckillSkuVo.getSkuId().toString(), redisTo);
                    //如果当前场次的商品库存已经上架，那么不需要上架了
                    // 分布式信号量：限流,如果redis不存在 才设置信号量
                    RSemaphore semaphore = redissonClient.getSemaphore(key);
                    semaphore.trySetPermits(seckillSkuVo.getSeckillCount().intValue());
                }
            });
        });
    }
}
