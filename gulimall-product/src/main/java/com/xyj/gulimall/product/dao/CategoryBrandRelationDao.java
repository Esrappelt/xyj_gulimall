package com.xyj.gulimall.product.dao;

import com.xyj.gulimall.product.entity.CategoryBrandRelationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 品牌分类关联
 * 
 * @author xyj
 * @email xyj@gmail.com
 * @date 2023-06-20 11:18:03
 */
@Mapper
public interface CategoryBrandRelationDao extends BaseMapper<CategoryBrandRelationEntity> {
    void updateCategory(@Param("catelog_id") Long catId, @Param("catelog_name")String name);
}
