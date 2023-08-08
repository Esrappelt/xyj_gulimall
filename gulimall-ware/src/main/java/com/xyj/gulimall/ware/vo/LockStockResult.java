package com.xyj.gulimall.ware.vo;

import lombok.Data;
import lombok.ToString;

/**
 * @Author jie
 * @Date 2023/8/1 10:07
 */
@Data
@ToString
public class LockStockResult {
    private Long skuId;
    private Integer num;
    private boolean locked;
}
