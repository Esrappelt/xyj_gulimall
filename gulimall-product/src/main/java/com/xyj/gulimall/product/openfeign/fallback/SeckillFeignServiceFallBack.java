package com.xyj.gulimall.product.openfeign.fallback;

import com.xyj.common.exception.BizCideEnume;
import com.xyj.common.utils.R;
import com.xyj.gulimall.product.openfeign.seckill.SeckillFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Author jie
 * @Date 2023/8/8 19:34
 */
@Slf4j
@Component
public class SeckillFeignServiceFallBack  implements SeckillFeignService {

    @Override
    public R getSkuSeckillInfo(Long skuId) {
        log.error("需要熔断 getSkuSeckillInfo错误！");
        return R.error(BizCideEnume.TOO_MANY_REQUEST.getCode(), BizCideEnume.TOO_MANY_REQUEST.getMsg());
    }
}
