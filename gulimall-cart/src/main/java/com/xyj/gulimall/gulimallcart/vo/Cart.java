package com.xyj.gulimall.gulimallcart.vo;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author jie
 * @Date 2023/7/30 11:08
 */
@Data
@ToString
public class Cart {

    private List<CartItem> items;
    private Integer countNum; //商品数量
    private Integer countType;//商品类型数量
    private BigDecimal totalAmount;//商品价格
    private BigDecimal reduce = new BigDecimal("0");//促销价格

    public Integer getCountNum() {
        int count = 0;
        if(items != null && items.size() > 0){
            for(CartItem item : items){
                count += item.getCount();
            }
        }
        return count;
    }

    public Integer getCountType() {
        return items.size();
    }

    public BigDecimal getTotalAmount() {
        // 计算购物项的总价格
        BigDecimal amount = new BigDecimal("0");
        if(items != null && items.size() > 0){
            for(CartItem item : items){
                if(item.getCheck()){
                    BigDecimal totalPrice = item.getTotalPrice();
                    amount = amount.add(totalPrice);
                }
            }
        }
        // 减去优惠价格
        amount = amount.subtract(this.getReduce());
        return amount;
    }
}
