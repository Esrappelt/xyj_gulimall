package com.xyj.gulimall.product.web;

import com.xyj.gulimall.product.service.SkuInfoService;
import com.xyj.gulimall.product.vo.SkuItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Author jie
 * @Date 2023/7/29 9:49
 */
@Controller
public class ItemController {
    @Autowired
    SkuInfoService skuInfoService;
    @GetMapping("/{skuId}.html")
    public String skuItem(@PathVariable("skuId") Long skuId, Model model){
        SkuItemVo skuItemVo = skuInfoService.getSkuItem(skuId);
        System.out.println(skuItemVo);
        model.addAttribute("item", skuItemVo);
        return "item";
    }
}
