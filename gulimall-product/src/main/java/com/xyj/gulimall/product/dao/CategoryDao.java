package com.xyj.gulimall.product.dao;

import com.xyj.gulimall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author xyj
 * @email xyj@gmail.com
 * @date 2023-06-19 20:55:29
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
