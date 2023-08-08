package com.xyj.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xyj.common.utils.PageUtils;
import com.xyj.gulimall.coupon.entity.SeckillSkuRelationEntity;

import java.util.Map;

/**
 * 秒杀活动商品关联
 *
 * @author xyj
 * @email xyj@gmail.com
 * @date 2023-06-15 18:04:51
 */
public interface SeckillSkuRelationService extends IService<SeckillSkuRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

