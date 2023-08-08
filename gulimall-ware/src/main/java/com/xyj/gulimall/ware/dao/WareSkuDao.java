package com.xyj.gulimall.ware.dao;

import com.xyj.gulimall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品库存
 *
 * @author xyj
 * @email xyj@gmail.com
 * @date 2023-06-15 18:36:07
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {

    Long getSkusStock(@Param("skuId") Long skuId);

    List<Long> listWareIdHasSkuStock(@Param("skuId") Long skuId);

    Long lockSkuStock(@Param("skuId")Long skuId, @Param("wareId")Long wareId, @Param("num")Integer num);

    void addStock(@Param("skuId")Long skuId, @Param("skuId")Long wareId, @Param("skuId")Integer skuNum);

    void unLockStock(@Param("skuId")Long skuId, @Param("wareId")Long wareId, @Param("skuNum")Integer skuNum, @Param("detailId")Long detailId);
}
