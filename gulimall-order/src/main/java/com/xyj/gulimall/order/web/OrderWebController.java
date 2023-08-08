package com.xyj.gulimall.order.web;

import com.xyj.gulimall.order.service.OrderService;
import com.xyj.gulimall.order.vo.OrderConfirmVo;
import com.xyj.gulimall.order.vo.OrderSubmitVo;
import com.xyj.gulimall.order.vo.SubmitOrderResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.concurrent.ExecutionException;

/**
 * @Author jie
 * @Date 2023/7/31 15:55
 */
@Controller
public class OrderWebController {
    @Autowired
    OrderService orderService;


    @GetMapping("/toTrade")
    public String toTrade(Model model) throws ExecutionException, InterruptedException {
        // 展示订单信息
        OrderConfirmVo orderConfirmVo = orderService.confirmOrder();
        model.addAttribute("confirmOrderData", orderConfirmVo);
        System.out.println("订单信息如下:" + orderConfirmVo);
        return "confirm";
    }

    /**
     * 创建订单，验证令牌，锁库存。。等等
     * 下单成功 去支付成功页
     * 失败 重新确认订单信息
     *
     * @param orderSubmitVo
     * @return
     */
    @PostMapping("/submitOrder")
    public String submitOrder(OrderSubmitVo orderSubmitVo, Model model, RedirectAttributes redirectAttributes)  {
        System.out.println("订单信息：" + orderSubmitVo);
        SubmitOrderResponseVo responseVo = orderService.submitOrder(orderSubmitVo);
        if (responseVo.getCode() == 0) {
            model.addAttribute("submitOrderResp", responseVo);
            return "pay";
        } else {
            String msg = "下单失败";
            switch (responseVo.getCode()) {
                case 1:
                    msg += "订单过期";
                    break;
                case 2:
                    msg += "订单价格变化";
                    break;
                case 3:
                    msg += "库存不足";
            }
            redirectAttributes.addFlashAttribute("msg", msg);
            return "redirect:http://order.gulimall.com/toTrade";
        }
    }
}
