package com.xyj.gulimall.member.openfeign;

import com.xyj.common.utils.R;
import org.springframework.cloud.openfeign.*;
import org.springframework.web.bind.annotation.RequestMapping;

// 首先声明远程调用接口,指定需要使用的服务名字，比如这里member想要调用coupon的服务
// 这样，其他服务在调用memberCoupons函数时，就通过http方式直接调用了!
// 只需要定义接口,需要给出完整的请求映射地址
@FeignClient("gulimall-coupon")
public interface CouponFeignService {
    @RequestMapping("/coupon/coupon/member/list")
    public R memberCoupons();
}
