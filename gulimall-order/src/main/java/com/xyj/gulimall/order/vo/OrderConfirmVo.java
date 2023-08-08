package com.xyj.gulimall.order.vo;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Author jie
 * @Date 2023/7/31 16:13
 */
@Data
@ToString
public class OrderConfirmVo {
    // ums_member_receive_address表
    List<MemberAddressVo> address;

    // 所有选中的购物项
    List<OrderItemVo> items;

    // 发票信息

    // 优惠券信息
    Integer integration;

    // 订单总额
    BigDecimal total;

    // 应付总额
    BigDecimal payPrice;

    // 订单令牌 防重复
    // TODO 订单防刷
    String orderToken;

    // 总数量
    Integer count;

    //
    Map<Long, Boolean> stocks;

    public Integer getCount() {
        Integer i = 0;
        if(items!=null){
            for (OrderItemVo item : items) {
                i += item.getCount();
            }
        }
        return count;
    }

    public BigDecimal getPayPrice() {
        return getTotal();
    }

    public BigDecimal getTotal() {
        BigDecimal total = new BigDecimal("0");
        if(items!=null){
            for (OrderItemVo item : items) {
                BigDecimal currentItemTotalPrice = item.getPrice().multiply(new BigDecimal(item.getCount().toString()));
                total = total.add(currentItemTotalPrice);
            }
        }
        return total;
    }
}
