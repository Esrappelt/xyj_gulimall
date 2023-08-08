package com.xyj.gulimall.product.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author jie
 * @Date 2023/8/8 0:03
 */
@Data
public class SeckillInfovo {
    /**
     * 活动id
     */
    private Long promotionId;
    /**
     * 活动场次id
     */
    private Long promotionSessionId;
    /**
     * 商品id
     */
    private Long skuId;
    /**
     * 秒杀价格
     */
    private BigDecimal seckillPrice;
    /**
     * 秒杀总量
     */
    private BigDecimal seckillCount;
    /**
     * 每人限购数量
     */
    private BigDecimal seckillLimit;
    /**
     * 排序
     */
    private Integer seckillSort;

    /**
     * 秒杀的开始时间
     */
    private Long startTime;
    /**
     * 秒杀的结束时间
     */
    private Long endTime;

    /**
     * 随机码
     */
    String randomCode;

}
