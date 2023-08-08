package com.xyj.gulimall.seckill.controller;

import com.xyj.common.utils.R;
import com.xyj.gulimall.seckill.service.SeckillService;
import com.xyj.gulimall.seckill.to.SeckillSkuRedisTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author jie
 * @Date 2023/8/7 22:55
 */
@Controller
public class SeckillController {
    @Autowired
    SeckillService seckillService;
    @GetMapping("/currentSeckillSkus")
    @ResponseBody
    public R getCurrentSeckillSkus(){
        List<SeckillSkuRedisTo> res = seckillService.getCurrentSeckillSkus();
        return R.ok().setData(res);
    }

    /**
     * 根据skuId查询商品是否参加秒杀活动
     * @param skuId
     * @return
     */
    @GetMapping(value = "/sku/seckill/{skuId}")
    @ResponseBody
    public R getSkuSeckillInfo(@PathVariable("skuId") Long skuId) {
        SeckillSkuRedisTo to = seckillService.getSkuSeckillInfo(skuId);
        return R.ok().setData(to);
    }

    @GetMapping("/kill")
    public String secKill(@RequestParam("killId")String killId,
                          @RequestParam("key")String key,
                          @RequestParam("num")Integer num,
                          Model model){
        // 判断是否登录
        String orderSn =  seckillService.kill(killId, key, num);
        model.addAttribute("orderSn", orderSn);
        return "success";
    }

}
