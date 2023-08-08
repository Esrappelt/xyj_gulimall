package com.xyj.gulimall.order.vo;

import com.xyj.gulimall.order.entity.OrderEntity;
import lombok.Data;

/**
 * @Author jie
 * @Date 2023/7/31 22:46
 */
@Data
public class SubmitOrderResponseVo {
    private OrderEntity order;
    private Integer code;//错误状态码，为0表示成功

}
