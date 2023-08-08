package com.xyj.common.to.mq;

import lombok.Data;

import java.util.List;

/**
 * @Author jie
 * @Date 2023/8/6 14:27
 */
@Data
public class StockLockedTo {
    private Long id; // 库存工作单id
    private StockDetailTo detail; //防止回滚后 工作单详情找不到了  暂时给存到MQ

}
