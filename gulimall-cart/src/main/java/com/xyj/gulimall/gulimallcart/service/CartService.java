package com.xyj.gulimall.gulimallcart.service;

import com.xyj.gulimall.gulimallcart.vo.Cart;
import com.xyj.gulimall.gulimallcart.vo.CartItem;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @Author jie
 * @Date 2023/7/30 11:24
 */
public interface CartService {
    void addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException;

    CartItem getCartItem(Long skuId);

    Cart getCart() throws ExecutionException, InterruptedException;


    void clearCart(String cartKey);

    void checkItem(Long skuId, Integer check);

    void changeCountItem(Long skuId, Integer num);

    void deleteItem(Long skuId);

    List<CartItem> getUserCartItems();
}
