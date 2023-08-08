package com.xyj.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xyj.common.utils.PageUtils;
import com.xyj.gulimall.order.entity.OrderEntity;
import com.xyj.common.to.SeckillOrderTo;
import com.xyj.gulimall.order.vo.OrderConfirmVo;
import com.xyj.gulimall.order.vo.OrderSubmitVo;
import com.xyj.gulimall.order.vo.PayVo;
import com.xyj.gulimall.order.vo.SubmitOrderResponseVo;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 订单
 *
 * @author xyj
 * @email xyj@gmail.com
 * @date 2023-06-15 18:32:36
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 订单确认页返回的数据
     * @return
     */
    OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException;

    SubmitOrderResponseVo submitOrder(OrderSubmitVo orderSubmitVo) ;


    OrderEntity getOrderSn(String orderSn);

    void closeOrder(OrderEntity orderEntity);

    PayVo getOrderPay(String orderSn);

    PageUtils queryPageWithItem(Map<String, Object> params);

    Boolean pay(PayVo payVo, String orderSn);

    void createSeckillOrder(SeckillOrderTo seckillOrder);
}

