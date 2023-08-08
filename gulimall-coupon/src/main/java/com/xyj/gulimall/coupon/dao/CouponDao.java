package com.xyj.gulimall.coupon.dao;

import com.xyj.gulimall.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author xyj
 * @email xyj@gmail.com
 * @date 2023-06-15 18:04:52
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
