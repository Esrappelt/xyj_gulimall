package com.xyj.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xyj.common.utils.PageUtils;
import com.xyj.gulimall.coupon.entity.SeckillSkuNoticeEntity;

import java.util.Map;

/**
 * 秒杀商品通知订阅
 *
 * @author xyj
 * @email xyj@gmail.com
 * @date 2023-06-15 18:04:51
 */
public interface SeckillSkuNoticeService extends IService<SeckillSkuNoticeEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

