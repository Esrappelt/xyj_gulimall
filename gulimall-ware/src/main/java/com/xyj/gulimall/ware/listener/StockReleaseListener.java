package com.xyj.gulimall.ware.listener;

import com.alibaba.fastjson.TypeReference;
import com.rabbitmq.client.Channel;
import com.xyj.common.to.OrderTo;
import com.xyj.common.to.mq.StockDetailTo;
import com.xyj.common.to.mq.StockLockedTo;
import com.xyj.common.utils.R;
import com.xyj.gulimall.ware.entity.WareOrderTaskDetailEntity;
import com.xyj.gulimall.ware.entity.WareOrderTaskEntity;
import com.xyj.gulimall.ware.service.WareSkuService;
import com.xyj.gulimall.ware.vo.OrderVo;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @Author jie
 * @Date 2023/8/6 16:40
 */
@RabbitListener(queues = "stock.release.stock.queue")
@Component
public class StockReleaseListener {

    @Autowired
    WareSkuService wareSkuService;

    @RabbitHandler
    public void handleStockLockedRelease(StockLockedTo stockLockedTo, Message message, Channel channel) throws IOException {
        try {
            wareSkuService.doUnLockStock(stockLockedTo);
            // 成功就手动确认
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }catch (Exception e){
            System.out.println(e.getMessage());
            // 没有成功就拒收消息 并放回队列
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }

    @RabbitHandler
    public void handleOrderCloseRelease(OrderTo order, Message message, Channel channel) throws IOException {
        System.out.println("订单关闭，库存解锁中...");
        try {
            wareSkuService.doUnLockStock(order);
            // 成功就手动确认
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }catch (Exception e){
            System.out.println(e.getMessage());
            // 没有成功就拒收消息 并放回队列
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }
}
