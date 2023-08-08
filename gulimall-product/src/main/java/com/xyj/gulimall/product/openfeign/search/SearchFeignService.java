package com.xyj.gulimall.product.openfeign.search;

import com.xyj.common.to.es.SkuEsModel;
import com.xyj.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @Author jie}
 * @Date 2023/6/26 0:23}
 */
@FeignClient("gulimall-search")
public interface SearchFeignService {
    @RequestMapping("/search/save/product")
    public R productStatusUp(@RequestBody List<SkuEsModel> skuEsModels);
}
