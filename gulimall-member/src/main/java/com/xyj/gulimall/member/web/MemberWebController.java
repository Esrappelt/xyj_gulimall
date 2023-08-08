package com.xyj.gulimall.member.web;

import com.alibaba.fastjson.JSON;
import com.xyj.common.utils.R;
import com.xyj.gulimall.member.openfeign.OrderFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author jie
 * @Date 2023/8/7 0:38
 */
@Controller
public class MemberWebController {
    @Autowired
    OrderFeignService orderFeignService;
    @GetMapping("/memberOrder.html")
    public String memberOrder(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum, Model model){
        Map<String, Object> params = new HashMap<>();
        params.put("page", pageNum);
        R r = orderFeignService.listWithItem(params);
        model.addAttribute("orders", r);
        return "orderList";
    }
}
