package com.xyj.gulimall.gulimallcart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.xyj.common.utils.R;
import com.xyj.gulimall.gulimallcart.feign.ProductFeignService;
import com.xyj.gulimall.gulimallcart.service.CartService;
import com.xyj.gulimall.gulimallcart.vo.Cart;
import com.xyj.gulimall.gulimallcart.vo.CartItem;
import com.xyj.gulimall.gulimallcart.vo.SkuInfoVo;
import com.xyj.gulimall.gulimallcart.vo.UserInfoTo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import static com.xyj.gulimall.gulimallcart.interceptor.CartInterceptor.threadLocal;

/**
 * @Author jie
 * @Date 2023/7/30 11:24
 */
@Slf4j
@Service
public class CartServiceImpl implements CartService {
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProductFeignService productFeignService;
    @Autowired
    ThreadPoolExecutor executor;


    private static final String CART_PREFIX = "gulimall:cart:";

    /**
     * 将商品添加到购物车
     * @param skuId
     * @param num
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Override
    public void addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        // 首先判断购物车是否有添加了该商品
        String res = (String) cartOps.get(skuId.toString());
        if(StringUtils.isEmpty(res)) {
            // 无此商品,则添加此商品
            CartItem cartItem = new CartItem();
            // 1.远程查询当前商品的信息
            CompletableFuture<Void> getBaseSkuInfo = CompletableFuture.runAsync(() -> {
                R info = productFeignService.info(skuId);
                SkuInfoVo skuInfo = info.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                });
                if (skuInfo != null) {
                    cartItem.setCheck(true);
                    cartItem.setCount(1);
                    cartItem.setImage(skuInfo.getSkuDefaultImg());
                    cartItem.setTitle(skuInfo.getSkuTitle());
                    cartItem.setSkuId(skuInfo.getSkuId());
                    cartItem.setPrice(skuInfo.getPrice());
                }
            }, executor);
            CompletableFuture<Void> getSkuSalueAttrValues = CompletableFuture.runAsync(() -> {
                List<String> skuSaleAttrValues = productFeignService.getSkuSaleAttrValues(skuId);
                cartItem.setSkuAttr(skuSaleAttrValues);
            }, executor);
            //等待异步查询完成
            CompletableFuture.allOf(getBaseSkuInfo, getSkuSalueAttrValues).get();
            String cartItemString = JSON.toJSONString(cartItem);
            cartOps.put(skuId.toString(), cartItemString);
//            return cartItem;
        }else {
            CartItem parsedCartItem = JSON.parseObject(res, CartItem.class);
            parsedCartItem.setCount(parsedCartItem.getCount() + num);
            cartOps.put(skuId.toString(), JSON.toJSONString(parsedCartItem));
//            return parsedCartItem;
        }
    }

    /**
     * 获取购物车项
     * @param skuId
     * @return
     */
    @Override
    public CartItem getCartItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        String res = (String) cartOps.get(skuId.toString());
        return JSON.parseObject(res, CartItem.class);
    }

    @Override
    public Cart getCart() throws ExecutionException, InterruptedException {
        Cart cart = new Cart();
        // 查询登录状态
        UserInfoTo userInfoTo = threadLocal.get();
        if (userInfoTo.getUserId() != null) {
            // 登录状态
            String cartKey = CART_PREFIX + userInfoTo.getUserId();
            String tempCartKey = CART_PREFIX + userInfoTo.getUserKey();
            List<CartItem> tempCartItems = getCartItems(tempCartKey);
            // 合并临时购物车
            if(tempCartItems != null && tempCartItems.size() > 0){
                for (CartItem tempCartItem : tempCartItems) {
                    addToCart(tempCartItem.getSkuId(), tempCartItem.getCount());
                }
                // 清空临时购物车数据
                clearCart(tempCartKey);
            }
            List<CartItem> cartItems = getCartItems(cartKey);
            cart.setItems(cartItems);
        }else {
            // 未登录状态
            String cartKey = CART_PREFIX + userInfoTo.getUserKey();
            List<CartItem> cartItems = getCartItems(cartKey);
            cart.setItems(cartItems);
        }
        return cart;
    }

    @Override
    public void clearCart(String cartKey) {
        stringRedisTemplate.delete(cartKey);
    }

    /**
     * 勾选购物项
     * @param skuId
     * @param check
     */
    @Override
    public void checkItem(Long skuId, Integer check) {
        CartItem cartItem = getCartItem(skuId);
        cartItem.setCheck(check == 1);
        String jsonString = JSON.toJSONString(cartItem);
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.put(skuId.toString(), jsonString);
    }

    /**
     * 修改购物项数量
     * @param skuId
     * @param num
     */
    @Override
    public void changeCountItem(Long skuId, Integer num) {
        CartItem cartItem = getCartItem(skuId);
        // 设置count

        cartItem.setCount(num);
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        //重新设置
        cartOps.put(skuId.toString(), JSON.toJSONString(cartItem));
    }

    @Override
    public void deleteItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.delete(skuId.toString());
    }

    @Override
    public List<CartItem> getUserCartItems() {
        UserInfoTo userInfoTo = threadLocal.get();
        System.out.println("登录信息:" +userInfoTo );
        if(userInfoTo.getUserId() == null){
            // 没有登录
            return null;
        }
        String cartKey = CART_PREFIX + userInfoTo.getUserId();
        List<CartItem> cartItems = getCartItems(cartKey);
        if(cartItems != null){
            // 购物车有东西
            // TODO 如果商品实时更新 那么需要远程调用商品服务 得到商品最新信息！
            return cartItems.stream().filter(CartItem::getCheck).collect(Collectors.toList());
        }
        return null;
    }

    private BoundHashOperations<String, Object, Object> getCartOps() {
        UserInfoTo userInfoTo = threadLocal.get();
        String cartKey = "";
        if (userInfoTo.getUserId() != null) {
            // 登录了
            cartKey = CART_PREFIX + userInfoTo.getUserId();
        } else {
            cartKey = CART_PREFIX + userInfoTo.getUserKey();
        }
        return stringRedisTemplate.boundHashOps(cartKey);
    }

    private List<CartItem> getCartItems(String cartKey){
        BoundHashOperations<String, Object, Object> cartOps = stringRedisTemplate.boundHashOps(cartKey);
        List<Object> values = cartOps.values(); // hash存储的直接得到一个List类型
        if(values != null && values.size() > 0){
            return values.stream().map(obj -> JSON.parseObject((String) obj, CartItem.class)).collect(Collectors.toList());
        }
        return null;
    }


}
