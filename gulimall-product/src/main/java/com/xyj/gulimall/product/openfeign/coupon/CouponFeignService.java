package com.xyj.gulimall.product.openfeign.coupon;

import com.xyj.common.to.SkuReductionTo;
import com.xyj.common.to.SpuBoundsTo;
import com.xyj.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author jie}
 * @Date 2023/6/22 11:49}
 */

/**
 * 我要调用coupon服务，需要coupon服务的application.name
 */
@FeignClient(name = "gulimall-coupon")
public interface CouponFeignService {
    /**
     * @param spuBoundsTo
     * @return
     * @RequestBody将SpuBoundsTo转为json数据 CouponFeignService向/coupon/spubounds/save接口发送post请求
     * 接口得到json数据后转为对象 然后进行操作
     * 因此，只要Json的数据模型是兼容的，双方服务无需使用同一个对象
     */
    @PostMapping("/coupon/spubounds/save")
    R saveSpuBounds(@RequestBody SpuBoundsTo spuBoundsTo);

    @PostMapping("/coupon/skufullreduction/saveInfo")
    R saveSkuReduction(@RequestBody SkuReductionTo skuReductionTo);
}
