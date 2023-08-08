package com.xyj.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xyj.common.utils.PageUtils;
import com.xyj.gulimall.product.entity.ProductAttrValueEntity;

import java.util.List;
import java.util.Map;

/**
 * spu属性值
 *
 * @author xyj
 * @email xyj@gmail.com
 * @date 2023-06-19 20:55:29
 */
public interface ProductAttrValueService extends IService<ProductAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveProductAttrValue(List<ProductAttrValueEntity> collect);

    List<ProductAttrValueEntity> getBaseListForSpu(Long spuId);

    void updateAttr(Long spuId, List<ProductAttrValueEntity> entities);
}

