package com.xyj.gulimall.order.feign;

import com.xyj.gulimall.order.vo.MemberAddressVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @Author jie
 * @Date 2023/7/31 16:24
 */
@FeignClient("gulimall-member")
public interface MemberFeignService {
    @GetMapping("/member/memberreceiveaddress/{memberId}")
    List<MemberAddressVo> getAddress(@PathVariable("memberId") Long memberId);
}
