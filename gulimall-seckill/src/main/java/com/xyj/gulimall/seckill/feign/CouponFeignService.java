package com.xyj.gulimall.seckill.feign;

import com.xyj.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author jie
 * @Date 2023/8/7 20:00
 */
@FeignClient("gulimall-coupon")
public interface CouponFeignService {
    @RequestMapping("/coupon/seckillsession/latest3DaySession")
    R getLatest3DaySession();
}
