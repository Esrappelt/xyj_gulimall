package com.xyj.gulimall.order.listener;

import com.rabbitmq.client.Channel;
import com.xyj.gulimall.order.service.OrderService;
import com.xyj.common.to.SeckillOrderTo;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@RabbitListener(queues = {"order.seckill.order.queue"})
@Component
public class ordeSeckillListener {
    @Autowired
    OrderService orderService;
    @RabbitHandler
    public void listener(SeckillOrderTo seckillOrder, Message message, Channel channel) throws IOException {
        // 保存订单
        try {
            log.info("准备创建秒杀商品的信息");
            orderService.createSeckillOrder(seckillOrder);
            // 手动确认ack，将数据从队列中删除了
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }catch (Exception e){
            // 手动确认失败ack,将数据返回给队列
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }
}
