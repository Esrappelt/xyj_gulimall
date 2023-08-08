package com.xyj.gulimall.product.vo;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.Arrays;

/**
 * @Author jie}
 * @Date 2023/6/20 15:32}
 */

/**
 * @EqualsAndHashCode(callSuper = true)
 * @ToString(callSuper = true)
 * 这两个参数代表继承类拥有重写hash和toString的能力
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
public class AttrRespVo extends AttrVo {
    /**
     * 需要多返回的东西
     * catelogName
     * groupName
     * catelogPath
     */
    private String catelogName;
    private String groupName;
    private Long[] catelogPath;




}
