package com.xyj.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xyj.common.utils.PageUtils;
import com.xyj.gulimall.product.entity.CategoryEntity;
import com.xyj.gulimall.product.vo.Catelog2Vo;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author xyj
 * @email xyj@gmail.com
 * @date 2023-06-15 16:16:56
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<CategoryEntity> listWithTree();

    void removeMenuByIds(List<Long> asList);

    Long[] fileCateLogPath(Long catelogId);

    void updateCascade(CategoryEntity category);

    List<CategoryEntity> getLevelOneCategories();

    Map<String, List<Catelog2Vo>> getCatalogJson();

}

