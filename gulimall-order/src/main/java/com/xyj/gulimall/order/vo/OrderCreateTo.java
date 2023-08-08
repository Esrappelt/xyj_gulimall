package com.xyj.gulimall.order.vo;

import com.xyj.gulimall.order.entity.OrderEntity;
import com.xyj.gulimall.order.entity.OrderItemEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author jie
 * @Date 2023/7/31 23:03
 */
@Data
public class OrderCreateTo {
    private OrderEntity order;
    private List<OrderItemEntity> orderItems;
    private BigDecimal payPrice;
    private BigDecimal fare;
}
