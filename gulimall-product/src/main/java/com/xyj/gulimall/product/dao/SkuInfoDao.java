package com.xyj.gulimall.product.dao;

import com.xyj.gulimall.product.entity.SkuInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * sku信息
 * 
 * @author xyj
 * @email xyj@gmail.com
 * @date 2023-06-19 20:55:29
 */
@Mapper
public interface SkuInfoDao extends BaseMapper<SkuInfoEntity> {

    void updateStatus(@Param("spuId") Long spuId, @Param("code") Integer code);
}
