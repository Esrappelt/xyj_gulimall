package com.xyj.gulimall.order.controller;

import com.xyj.gulimall.order.entity.OrderEntity;
import com.xyj.gulimall.order.entity.OrderReturnReasonEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.UUID;

/**
 * @Author jie
 * @Date 2023/7/31 13:05
 */
@Slf4j
@RestController
public class RabbitController {
    @Autowired
    RabbitTemplate rabbitTemplate;

    @GetMapping("/sendMq")
    public String sendMq(@RequestParam(value = "num", defaultValue = "10") Integer num){
        for (int i = 0; i < num; i++) {
            if (i % 2 == 0) {
                OrderReturnReasonEntity orderReturnReasonEntity = new OrderReturnReasonEntity();
                orderReturnReasonEntity.setName("orderReturnReasonEntity" + i);
                orderReturnReasonEntity.setCreateTime(new Date());
                rabbitTemplate.convertAndSend("my.exchange.direct",
                        "hello.java",
                        orderReturnReasonEntity,
                        new CorrelationData(UUID.randomUUID().toString()));
                log.info("消息发送完成!");
            } else {
                OrderEntity orderEntity = new OrderEntity();
                orderEntity.setMemberUsername("orderEntity" + i);
                orderEntity.setCreateTime(new Date());
                // CorrelationData可以进行标识ID，可以存入数据库 定时查看有哪些没有抵达的数据
                rabbitTemplate.convertAndSend("my.exchange.direct",
                        "hello.java",
                        orderEntity,
                        new CorrelationData(UUID.randomUUID().toString()));
                log.info("消息发送完成!");
            }
        }
        return "ok";
    }
}
