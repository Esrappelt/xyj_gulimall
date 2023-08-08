package com.xyj.gulimall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author jie
 * @Date 2023/7/31 22:27
 */
@Data
public class OrderSubmitVo {
    private Long addrId;// 地址id
    private Integer payType; // 支付方式
    //无需提交商品，而是去从购物车重新获取！
    // 优惠、发票等等
    private String orderToken;//必须携带令牌 防重
    private BigDecimal payPrice;//应付金额
    // 用户相关的信息,直接去session取
    private String note;//订单备注
}
