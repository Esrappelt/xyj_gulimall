package com.xyj.gulimall.order.listener;

import com.rabbitmq.client.Channel;
import com.xyj.gulimall.order.entity.OrderEntity;
import com.xyj.gulimall.order.service.OrderService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @Author jie
 * @Date 2023/8/6 20:45
 */
@RabbitListener(queues = {"order.release.order.queue"})
@Component
public class orderCloseListener {
    @Autowired
    OrderService orderService;
    @RabbitHandler
    public void listener(OrderEntity orderEntity, Message message, Channel channel) throws IOException {

        // 关闭订单
        try {
            orderService.closeOrder(orderEntity);
            // 手动确认ack，将数据从队列中删除了
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }catch (Exception e){
            // 手动确认失败ack,将数据返回给队列
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }
}
