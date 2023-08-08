package com.xyj.gulimall.order.dao;

import com.xyj.gulimall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 订单
 * 
 * @author xyj
 * @email xyj@gmail.com
 * @date 2023-06-15 18:32:36
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {

    OrderEntity getOrderSn(@Param("orderSn") String orderSn);

    void updateOrderStatus(@Param("orderSn") String orderSn, @Param("code")Integer code);
}
