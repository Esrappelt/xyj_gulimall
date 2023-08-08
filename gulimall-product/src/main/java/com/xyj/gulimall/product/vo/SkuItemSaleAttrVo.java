package com.xyj.gulimall.product.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;


/**
 * @Author jie
 * @Date 2023/7/29 11:50
 */
@Data
@ToString
public class SkuItemSaleAttrVo {
    private Long attrId;
    private String attrName;
    private List<String> attrValues;
}
