package com.xyj.gulimall.order.vo;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author jie
 * @Date 2023/7/31 16:17
 */
@Data
@ToString
public class OrderItemVo {
    private Long skuId;
    private Boolean check;
    private String title;
    private String image;
    private List<String> skuAttr;
    private BigDecimal price;
    private Integer count;
    private BigDecimal totalPrice;
    private boolean hasStock;
    private BigDecimal weight;
}
