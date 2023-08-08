package com.xyj.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xyj.common.to.OrderTo;
import com.xyj.common.to.mq.StockLockedTo;
import com.xyj.common.utils.PageUtils;
import com.xyj.gulimall.ware.entity.WareSkuEntity;
import com.xyj.gulimall.ware.vo.LockStockResult;
import com.xyj.gulimall.ware.vo.SkuHasStockVo;
import com.xyj.gulimall.ware.vo.WareSkuLockVo;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author xyj
 * @email xyj@gmail.com
 * @date 2023-06-15 18:36:07
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageByCondition(Map<String, Object> params);

    void addStock(Long skuId, Long wareId, Integer skuNum);

    List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds);

    Boolean orderLockStock(WareSkuLockVo wareSkuLockVo);


    void doUnLockStock(StockLockedTo stockLockedTo);

    void doUnLockStock(OrderTo order);
}

