package com.xyj.gulimall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @Author jie
 * @Date 2023/8/1 10:03
 */
@Data
public class WareSkuLockVo {
    private String orderSn;// 订单号

    private List<OrderItemVo> locks; // 需要锁的东西

}
