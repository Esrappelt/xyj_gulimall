package com.xyj.gulimall.product.openfeign.seckill;

import com.xyj.common.utils.R;
import com.xyj.gulimall.product.openfeign.fallback.SeckillFeignServiceFallBack;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Author jie
 * @Date 2023/8/8 0:00
 */
@FeignClient(value = "gulimall-seckill", fallback = SeckillFeignServiceFallBack.class)
public interface SeckillFeignService {
    @GetMapping(value = "/sku/seckill/{skuId}")
    R getSkuSeckillInfo(@PathVariable("skuId") Long skuId);
}
