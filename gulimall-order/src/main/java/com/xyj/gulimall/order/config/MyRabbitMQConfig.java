package com.xyj.gulimall.order.config;

import com.rabbitmq.client.Channel;
import com.xyj.gulimall.order.entity.OrderEntity;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author jie
 * @Date 2023/8/6 9:49
 */
@Configuration
public class MyRabbitMQConfig {

    /**
     * 如果mq里面没有，则自动创建
     *
     * @return Queue
     */
    @Bean
    public Queue orderDelayQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", "order-event-exchange");
        args.put("x-dead-letter-routing-key", "order.release.order");
        args.put("x-message-ttl", 60000);
        // String name, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments
        return new Queue("order.delay.queue", true, false, false, args);
    }

    @Bean
    public Queue orderReleaseOrderQueue() {
        return new Queue("order.release.order.queue", true, false, false);
    }


    @Bean
    public Queue orderSeckillOrderQueue() {
        return new Queue("order.seckill.order.queue", true, false, false);
    }

    @Bean
    public Exchange orderEventExchange() {
        // Topic类型的交换机
        //String name, boolean durable, boolean autoDelete, Map<String, Object> arguments
        return new TopicExchange("order-event-exchange", true, false);
    }

    @Bean
    public Binding orderCreateOrderBindings() {
        // String destination, DestinationType, String exchange, String routingKey, Map<String, Object> arguments
        return new Binding("order.delay.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.create.order",
                null);
    }

    @Bean
    public Binding orderReleaseOrderBindings() {
        // String destination, DestinationType, String exchange, String routingKey, Map<String, Object> arguments
        return new Binding("order.release.order.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.release.order",
                null);
    }

    /**
     * 订单释放和库存释放的绑定
     * 订单释放后 路由到库存释放队列
     * @return
     */
    @Bean
    public Binding orderReleaseOtherBindings() {
        // String destination, DestinationType, String exchange, String routingKey, Map<String, Object> arguments
        return new Binding("stock.release.stock.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.release.other.#",
                null);
    }




    /**
     * 订单释放和库存释放的绑定
     * 订单释放后 路由到库存释放队列
     * @return
     */
    @Bean
    public Binding orderSeckillOrderBindings() {
        // String destination, DestinationType, String exchange, String routingKey, Map<String, Object> arguments
        return new Binding("order.seckill.order.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.seckill.order",
                null);
    }








}
