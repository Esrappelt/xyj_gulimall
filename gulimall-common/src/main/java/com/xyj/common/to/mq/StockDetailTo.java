package com.xyj.common.to.mq;

import lombok.Data;

/**
 * @Author jie
 * @Date 2023/8/6 14:40
 */
@Data
public class StockDetailTo {
    private Long id;
    /**
     * sku_id
     */
    private Long skuId;
    /**
     * sku_name
     */
    private String skuName;
    /**
     * 购买个数
     */
    private Integer skuNum;
    /**
     * 工作单id
     */
    private Long taskId;
    /**
     * 仓库ID
     */
    private Long wareId;
    /**
     * 1-锁定 2解锁 3扣除
     */
    private Long lockStatus;
}
