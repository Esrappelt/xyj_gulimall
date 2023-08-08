package com.xyj.gulimall.ware.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Author jie}
 * @Date 2023/6/23 0:14}
 */
@Data
public class PurchaseDoneVo {
    @NotNull
    private Long id; // 采购单id

    private List<PurchaseItemDoneVo> items;// 采购项

}
