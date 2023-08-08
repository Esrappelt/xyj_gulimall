package com.xyj.gulimall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @Author jie}
 * @Date 2023/6/22 23:03}
 */
@Data
public class MergeVo {
    private Long purchaseId;
    private List<Long> items;
}
