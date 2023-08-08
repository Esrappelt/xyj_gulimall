package com.xyj.gulimall.seckill.to;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.xyj.gulimall.seckill.vo.SkuInfoVo;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author jie
 * @Date 2023/8/7 20:56
 */
@Data
public class SeckillSkuRedisTo {
    private Long id;
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
     * sku详细信息
     */
    private SkuInfoVo skuInfo;

    /**
     * 秒杀的开始时间
     */
    private Long startTime;
    /**
     * 秒杀的结束时间
     */
    private Long endTime;

    /**
     * 秒杀的随机码
     */
    private String randomCode;

}
