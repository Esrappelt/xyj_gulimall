package com.xyj.gulimall.product.vo;

import com.xyj.gulimall.product.entity.SkuImagesEntity;
import com.xyj.gulimall.product.entity.SkuInfoEntity;
import com.xyj.gulimall.product.entity.SpuInfoDescEntity;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @Author jie
 * @Date 2023/7/29 10:04
 */
@Data
@ToString
public class SkuItemVo {
    // 1.sku基本信息获取 pms_sku_info
    SkuInfoEntity info;
    private boolean hasStock = true;
    // 2.sku图片信息psm_sku_images
    List<SkuImagesEntity> images;
    // 3.spu的销售属性
    List<SkuItemSaleAttrVo> saleAttr;
    // 4.spu的介绍
    SpuInfoDescEntity desc;
    // 5.spu的规格参数信息
    List<SpuItemAttrGroupVo> groupAttrs;
    //6、秒杀商品的优惠信息
    SeckillInfovo seckillInfo;

}
