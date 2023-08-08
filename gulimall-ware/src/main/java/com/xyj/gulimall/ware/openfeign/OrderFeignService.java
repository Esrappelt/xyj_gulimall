package com.xyj.gulimall.ware.openfeign;

import com.xyj.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Author jie
 * @Date 2023/8/6 15:49
 */
@FeignClient("gulimall-order")
public interface OrderFeignService {

    @GetMapping("/order/order/status/{orderSn}")
    R getOrderSn(@PathVariable String orderSn);
}
