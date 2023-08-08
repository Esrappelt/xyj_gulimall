package com.xyj.gulimall.order.web;

import com.xyj.common.utils.R;
import com.xyj.gulimall.order.entity.OrderEntity;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.UUID;

/**
 * @Author jie
 * @Date 2023/7/31 15:00
 */
@Controller
public class HelloController {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @GetMapping("/{page}.html")
    public String listPage(@PathVariable("page") String page){
        return page;
    }

    @GetMapping("/test/create/order")
    @ResponseBody
    public R createOrderTest(){
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderSn(UUID.randomUUID().toString());
        orderEntity.setModifyTime(new Date());
        // 给MQ发送消息
        rabbitTemplate.convertAndSend("order-even-exchange", "order.create.order", orderEntity);
        return R.ok();
    }
}
