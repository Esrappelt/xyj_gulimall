package com.xyj.gulimall.authserver.feign;

import com.xyj.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author jie
 * @Date 2023/7/29 17:45
 */
@FeignClient("gulimall-third-party")
public interface ThirdPartFeignService {
    @GetMapping("/sms/sendCode")
    R sendCode(@RequestParam("phone") String phone, @RequestParam("code")String code);
}
