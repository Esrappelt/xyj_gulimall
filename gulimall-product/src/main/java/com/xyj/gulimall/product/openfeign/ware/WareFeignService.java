package com.xyj.gulimall.product.openfeign.ware;

import com.xyj.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @Author jie}
 * @Date 2023/6/25 22:55}
 */

/**
 * 库存服务功能
 */
@FeignClient("gulimall-ware")
public interface WareFeignService {

    /**
     * 查询是否有库存
     */
    @PostMapping("/ware/waresku/hasStock")
    R getSkuHasStock(@RequestBody List<Long> skuIds);
}
