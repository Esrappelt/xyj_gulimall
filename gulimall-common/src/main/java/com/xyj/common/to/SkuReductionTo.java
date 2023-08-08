package com.xyj.common.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author jie}
 * @Date 2023/6/22 12:37}
 */
@Data
public class SkuReductionTo {
    private Long skuId;
    private int fullCount;
    private BigDecimal discount;
    private int countStatus;
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private int priceStatus;
    private List<MemberPrice> memberPrice;
}
