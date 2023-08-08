package com.xyj.gulimall.product.dao;

import com.xyj.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 属性&属性分组关联
 * 
 * @author xyj
 * @email xyj@gmail.com
 * @date 2023-06-19 20:55:29
 */
@Mapper
public interface AttrAttrgroupRelationDao extends BaseMapper<AttrAttrgroupRelationEntity> {

    void deleteRelationEntity(@Param("relationEntityList") List<AttrAttrgroupRelationEntity> relationEntityList);
}
