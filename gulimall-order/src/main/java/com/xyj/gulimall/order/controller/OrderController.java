package com.xyj.gulimall.order.controller;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.xyj.gulimall.order.entity.OrderEntity;
import com.xyj.gulimall.order.service.OrderService;
import com.xyj.common.utils.PageUtils;
import com.xyj.common.utils.R;



/**
 * 订单
 *
 * @author xyj
 * @email xyj@gmail.com
 * @date 2023-06-15 18:32:36
 */
@RestController
@RequestMapping("order/order")
public class OrderController {
    @Autowired
    private OrderService orderService;


    @GetMapping("/status/{orderSn}")
    public R getOrderSn(@PathVariable("orderSn") String orderSn){
        OrderEntity order = orderService.getOrderSn(orderSn);
        return R.ok().setData(order);

    }
    /**
     * 查询当前登录用户的订单信息
     */
    @RequestMapping ("/listWithItem")
    public R listWithItem(@RequestParam Map<String, Object> params){
        PageUtils page = orderService.queryPageWithItem(params);

        return R.ok().put("page", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = orderService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		OrderEntity order = orderService.getById(id);

        return R.ok().put("order", order);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody OrderEntity order){
		orderService.save(order);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody OrderEntity order){
		orderService.updateById(order);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		orderService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
