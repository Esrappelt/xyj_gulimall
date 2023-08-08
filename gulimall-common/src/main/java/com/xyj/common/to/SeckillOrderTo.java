package com.xyj.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author jie
 * @Date 2023/8/8 14:24
 */
@Data
public class SeckillOrderTo {
    /**
     * 订单号
     */
    private String orderSn;
    /**
     * 活动场次
     */
    private Long promotionSessionId;
    /**
     * 商品Id
     */
    private Long skuId;
    /**
     * 商品价格
     */
    private BigDecimal seckillPrice;
    /**
     * 购买的数量
     */
    private Integer num;
    /**
     * 会员用户Id
     */
    private Long memberId;
}
