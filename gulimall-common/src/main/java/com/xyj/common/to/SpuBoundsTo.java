package com.xyj.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author jie}
 * @Date 2023/6/22 11:59}
 */
@Data
public class SpuBoundsTo {
    private Long spuId;
    private BigDecimal buyBounds;
    private BigDecimal growBounds;
}
