package com.xyj.gulimall.order.web;

import com.xyj.gulimall.order.entity.OrderEntity;
import com.xyj.gulimall.order.entity.PaymentInfoEntity;
import com.xyj.gulimall.order.service.OrderService;
import com.xyj.gulimall.order.service.PaymentInfoService;
import com.xyj.gulimall.order.vo.PayVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Random;
import java.util.UUID;


@Slf4j
@Controller
public class PayWebController {

    @Autowired
    OrderService orderService;


    /**
     * 用户下单:支付宝支付
     * 1、让支付页让浏览器展示
     * 2、支付成功以后，跳转到用户的订单列表页
     *
     * @param orderSn
     * @return
     */
    @GetMapping(value = "/aliPayOrder")
    public String aliPayOrder(@RequestParam("orderSn") String orderSn) throws InterruptedException {
        // 获取订单详情
        PayVo payVo = orderService.getOrderPay(orderSn);
        // 假装支付
        Boolean payed = orderService.pay(payVo, orderSn);
        if (!payed) {
            return "redirect:http://order.gulimall.com/pay_failed.html";
        } else {
            return "redirect:http://member.gulimall.com/memberOrder.html";
        }
    }

    @GetMapping(value = "/pay_failed.html")
    public String payFailed() {
        return "pay_failed";
    }

    //根据订单号查询订单状态的API
    @GetMapping(value = "/queryByOrderId")
    @ResponseBody
    public OrderEntity queryByOrderId(@RequestParam("orderId") String orderId) {
        log.info("查询支付记录...");
        return orderService.getOrderSn(orderId);
    }
}
