package com.xyj.gulimall.gulimallcart.controller;

import com.netflix.ribbon.proxy.annotation.Http;
import com.xyj.gulimall.gulimallcart.interceptor.CartInterceptor;
import com.xyj.gulimall.gulimallcart.service.CartService;
import com.xyj.gulimall.gulimallcart.vo.Cart;
import com.xyj.gulimall.gulimallcart.vo.CartItem;
import com.xyj.gulimall.gulimallcart.vo.UserInfoTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @Author jie
 * @Date 2023/7/30 11:28
 */
@Controller
public class CartController {
    @Autowired
    CartService cartService;

    @GetMapping("/currentUserCartItems")
    @ResponseBody
    public List<CartItem> getCurrentUserCartItems(){
        return cartService.getUserCartItems();
    }


    @GetMapping("/cart.html")
    public String cartListPage(Model model){
        try {
            Cart cart = cartService.getCart();
            model.addAttribute("cart", cart);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return "cartList";
    }

    /**
     * 添加商品到购物车
     * @return
     */
    @GetMapping("/addToCart")
    public String addToCart(@RequestParam("skuId") Long skuId, @RequestParam("num") Integer num, RedirectAttributes redirectAttributes){
        try {
            cartService.addToCart(skuId, num);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        // 重定向携带数据必须使用RedirectAttributes
        redirectAttributes.addAttribute("skuId", skuId);
        return "redirect:http://cart.gulimall.com/addToCartSuccess.html";
    }

    @GetMapping("/addToCartSuccess.html")
    public String addToCartSuccessPage(@RequestParam("skuId") Long skuId, Model model){
        CartItem cartItem = cartService.getCartItem(skuId);
        model.addAttribute("cartItem", cartItem);
        return "success";
    }

    @GetMapping("/checkItem")
    public String checkItem(@RequestParam("skuId") Long skuId, @RequestParam("check") Integer check){
        cartService.checkItem(skuId, check);
        return "redirect:http://cart.gulimall.com/cart.html";
    }

    @GetMapping("/countItem")
    public String countItem(@RequestParam("skuId")Long skuId, @RequestParam("num")Integer num){
        cartService.changeCountItem(skuId, num);
        return "redirect:http://cart.gulimall.com/cart.html";
    }

    @GetMapping("/deleteItem")
    public String deleteItem(@RequestParam("skuId")Long skuId){
        cartService.deleteItem(skuId);
        return "redirect:http://cart.gulimall.com/cart.html";
    }
}
