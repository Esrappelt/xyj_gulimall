package com.xyj.gulimall.member.openfeign;

import com.xyj.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @Author jie
 * @Date 2023/8/7 9:55
 */
@FeignClient("gulimall-order")
public interface OrderFeignService {
    @RequestMapping("/order/order/listWithItem")
    R listWithItem(@RequestParam Map<String, Object> params);
}
