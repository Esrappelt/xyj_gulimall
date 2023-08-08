package com.xyj.gulimall.product.dao;

import com.xyj.gulimall.product.entity.AttrEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 商品属性
 * 
 * @author xyj
 * @email xyj@gmail.com
 * @date 2023-06-19 20:55:29
 */
@Mapper
public interface AttrDao extends BaseMapper<AttrEntity> {
    List<Long> selectAttrIds(@Param("attrIds") List<Long> attrIds);
}
