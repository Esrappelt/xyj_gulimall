package com.xyj.gulimall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author jie
 * @Date 2023/7/31 23:11
 */
@Data
public class FareVo {
    private MemberAddressVo address;
    private BigDecimal fare;
}
