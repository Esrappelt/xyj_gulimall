package com.xyj.gulimall.order.feign;

import com.xyj.gulimall.order.vo.OrderItemVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @Author jie
 * @Date 2023/7/31 16:43
 */
@FeignClient("gulimall-cart")
@Component
public interface CartFeignService {
    @GetMapping("/currentUserCartItems")
    List<OrderItemVo> getCurrentUserCartItems();
}
