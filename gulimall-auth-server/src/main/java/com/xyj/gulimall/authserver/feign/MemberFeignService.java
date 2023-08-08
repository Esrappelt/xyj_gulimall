package com.xyj.gulimall.authserver.feign;

import com.xyj.common.utils.R;
import com.xyj.gulimall.authserver.vo.UserLoginVo;
import com.xyj.gulimall.authserver.vo.UserRegistVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author jie
 * @Date 2023/7/29 20:40
 */
@FeignClient("gulimall-member")
public interface MemberFeignService {

    @PostMapping("/member/member/register")
    R register(@RequestBody UserRegistVo userRegistVo);
    @PostMapping("/member/member/login")
    R login(@RequestBody UserLoginVo userLoginVo);
}
