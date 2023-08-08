package com.xyj.gulimal.gulimallsearch.controller;

import com.xyj.common.exception.BizCideEnume;
import com.xyj.common.to.es.SkuEsModel;
import com.xyj.common.utils.R;
import com.xyj.gulimal.gulimallsearch.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author jie}
 * @Date 2023/6/25 23:30}
 */
@RestController
@Slf4j
@RequestMapping("/search/save")
public class ElasticSaveController {
    @Autowired
    ProductSaveService productSaveService;
    /**
     * 上架商品
     */
    @RequestMapping("/product")
    public R productStatusUp(@RequestBody List<SkuEsModel> skuEsModels){
        boolean b;
        try {
            b = productSaveService.productStatusUp(skuEsModels);
        }catch (Exception e){
            log.error("商品上架失败:{}", e.getMessage());
            return R.error(BizCideEnume.PRODUCT_UP_EXCEPTION.getCode(), BizCideEnume.PRODUCT_UP_EXCEPTION.getMsg());
        }
        return b ? R.ok() : R.error(BizCideEnume.PRODUCT_UP_EXCEPTION.getCode(), BizCideEnume.PRODUCT_UP_EXCEPTION.getMsg());
    }
}
