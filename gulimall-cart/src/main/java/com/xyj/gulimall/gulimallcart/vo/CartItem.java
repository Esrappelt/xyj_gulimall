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
public class CartItem {
    private Long skuId;
    private Boolean check = true;
    private String title;
    private String image;
    private List<String> skuAttr;
    private BigDecimal price;
    private Integer count;
    private BigDecimal totalPrice;
    public BigDecimal getTotalPrice() {
        return this.price.multiply(new BigDecimal(String.valueOf(this.count)));
    }
}
