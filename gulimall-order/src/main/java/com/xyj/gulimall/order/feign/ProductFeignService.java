package com.xyj.gulimall.order.feign;

import com.xyj.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author jie
 * @Date 2023/7/31 23:41
 */
@FeignClient("gulimall-product")
public interface ProductFeignService {
    @RequestMapping("/product/spuinfo/skuId/{id}")
    R getSpuInfoBySkuId(@PathVariable("id") Long id);
}
