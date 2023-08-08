package com.xyj.gulimall.order.service.impl;

import com.rabbitmq.client.Channel;
import com.xyj.gulimall.order.entity.OrderEntity;
import com.xyj.gulimall.order.entity.OrderReturnReasonEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xyj.common.utils.PageUtils;
import com.xyj.common.utils.Query;

import com.xyj.gulimall.order.dao.OrderItemDao;
import com.xyj.gulimall.order.entity.OrderItemEntity;
import com.xyj.gulimall.order.service.OrderItemService;

@Slf4j
@Service("orderItemService")
@RabbitListener(queues = {"hello-queue"})
public class OrderItemServiceImpl extends ServiceImpl<OrderItemDao, OrderItemEntity> implements OrderItemService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderItemEntity> page = this.page(
                new Query<OrderItemEntity>().getPage(params),
                new QueryWrapper<OrderItemEntity>()
        );

        return new PageUtils(page);
    }
    /**
     * 监听队列的消息
     * hello-queue
     * 得到结果在Message类包装
     * 这个是只有一个人能接收到消息  收到消息就会删除了 其他人接收不到
     */
    @RabbitHandler
    void receiveMsg(Message message, OrderReturnReasonEntity res, Channel channel) throws InterruptedException {
//        Thread.sleep(3000);// 一个消息处理完才可以接收下个消息
        log.info("消息接收完成，结果是:{}", res.getName());
//        log.info("通道信息:{}", channel);
//        log.info("详细消息:{}", message);
    }

    /**
     * 每一个重载方法 接收的方法不同
     * @param message
     * @param res
     * @param channel
     * @throws InterruptedException
     */
    @RabbitHandler
    void receiveMsg2(Message message, OrderEntity res, Channel channel) {
        log.info("消息接收完成，结果是:{}", res.getMemberUsername());
        long deliveryTag = message.getMessageProperties().getDeliveryTag();// 自增的Tag
        log.info("签收了:" + deliveryTag);
        try {
            channel.basicAck(deliveryTag, false);// 回复ack  接收当前消息
//            channel.basicNack(deliveryTag, false, false);// 拒绝接收消息
        } catch (IOException e) {
            // 接收消息错误，网络出问题
            throw new RuntimeException(e);
        }
    }
}