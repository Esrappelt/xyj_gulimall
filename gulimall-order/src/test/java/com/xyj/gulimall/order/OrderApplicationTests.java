package com.xyj.gulimall.order;

import com.xyj.gulimall.order.entity.OrderEntity;
import com.xyj.gulimall.order.entity.OrderReturnReasonEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.Objects;

@Slf4j
@SpringBootTest
class OrderApplicationTests {

    @Autowired
    AmqpAdmin amqpAdmin;

    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * Exchange,Queue,Binding
     * AmqpAdmin进行管理交换机
     */

    @Test
    void createExchange() {
        DirectExchange directExchange = new DirectExchange("my.exchange.direct");
        amqpAdmin.declareExchange(directExchange);
        log.info("directExchange:{}", directExchange);
    }

    @Test
    void createQueue() {
        Queue queue = new Queue("hello-queue");
        amqpAdmin.declareQueue(queue);
        log.info("queue:{}", queue);
    }

    @Test
    void createBinding() {
        //String destination, Binding.DestinationType destinationType, String exchange, String routingKey, Map<String, Object> arguments
        Binding binding = new Binding("hello-queue", // 需要绑定的队列
                Binding.DestinationType.QUEUE, //对队列进行绑定
                "my.exchange.direct",//要绑定的交换机
                "hello.java",// 路由键
                null);
        amqpAdmin.declareBinding(binding);
        log.info("binding:{}", binding);
    }

    @Test
    void sendMsg() {
        for (int i = 0; i < 10; i++) {
            if (i % 2 == 0) {
                OrderReturnReasonEntity orderReturnReasonEntity = new OrderReturnReasonEntity();
                orderReturnReasonEntity.setName("orderReturnReasonEntity" + i);
                orderReturnReasonEntity.setCreateTime(new Date());
                rabbitTemplate.convertAndSend("my.exchange.direct", "hello.java", orderReturnReasonEntity);
                log.info("消息发送完成!");
            } else {
                OrderEntity orderEntity = new OrderEntity();
                orderEntity.setMemberUsername("orderEntity" + i);
                orderEntity.setCreateTime(new Date());
                rabbitTemplate.convertAndSend("my.exchange.direct", "hello.java", orderEntity);
                log.info("消息发送完成!");
            }
        }
    }


}
