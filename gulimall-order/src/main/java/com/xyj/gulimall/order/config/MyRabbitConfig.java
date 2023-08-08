package com.xyj.gulimall.order.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.annotation.PostConstruct;

/**
 * @Author jie
 * @Date 2023/7/31 11:10
 */
@Slf4j
@Configuration
public class MyRabbitConfig {
    @Autowired
    RabbitTemplate rabbitTemplate;

    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 定制RabbitTemplate
     * 服务段和消费端确认机制
     *
     * 在自动ack模式下，消费端默认都会直接ack  所以消息可能会丢失
     * 在手动ack模式下，只要消费端没有明确接收消息，消息就不会丢失，也就是unacked状态。 宕机后 状态变为ready状态，重新链接后消息会重发
     */
    @PostConstruct // MyRabbitConfig对象创建完成后，执行这个方法
    public void initRabbitTemplate(){
        // 这是客户端抵达Broker代理服务器Exchange交换机时，返回ack=true
        RabbitTemplate.ConfirmCallback confirmCallback = new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean b, String s) {
                log.info("confirm:correlationData:[" + "]," + "ack=" + b + ",cause:" + s);
            }
        };
        // 消息抵达队列时 返回确认ack
        RabbitTemplate.ReturnCallback returnCallback = new RabbitTemplate.ReturnCallback() {
            // 如果消息没有抵达，则触发失败发送信息的回调信息，失败的消息的详细内容
            /**
             *
             * @param message 失败的消息的详细内容
             * @param i 状态码
             * @param s 文本内容
             * @param s1 交换机
             * @param s2 路由键
             */
            @Override
            public void returnedMessage(Message message, int i, String s, String s1, String s2) {
                log.info("FailMessage[" + message + "]," + "code:" + i +",内容:" + s + ",Exchange:" + s1 + ",路由键:" + s2);
            }
        };
        rabbitTemplate.setConfirmCallback(confirmCallback);
        rabbitTemplate.setReturnCallback(returnCallback);
    }
}
