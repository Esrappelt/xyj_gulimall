package com.xyj.gulimall.ware.vo;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * @Author jie
 * @Date 2023/7/31 19:52
 */
@Data
@ToString
public class FareVo {
    private MemberAddressVo address;
    private BigDecimal fare;

}
