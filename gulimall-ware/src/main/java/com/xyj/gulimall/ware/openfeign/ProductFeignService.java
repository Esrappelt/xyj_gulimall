package com.xyj.gulimall.ware.openfeign;

import com.xyj.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author jie}
 * @Date 2023/6/23 1:14}
 */
@FeignClient("gulimall-gateway")
public interface ProductFeignService {
    /**
     * 1.可以给网关发送请求 @FeignClient("gulimall-gateway")
     * /api/product/skuinfo/info/{skuId}
     * 2. 可以不经过网关，直接后台处理 @FeignClient("gulimall-product")
     * /product/skuinfo/info/{skuId}
     * @param skuId
     * @return
     */
    @RequestMapping("/api/product/skuinfo/info/{skuId}")
    public R info(@PathVariable("skuId") Long skuId);
}
