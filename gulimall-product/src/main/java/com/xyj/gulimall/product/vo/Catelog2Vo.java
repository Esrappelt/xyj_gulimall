package com.xyj.gulimall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * @Author jie}
 * @Date 2023/6/26 11:49}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Catelog2Vo {
    private String catalog1Id;
    private List<Catalog3Vo> catalog3List;
    private String id;
    private String name;
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class Catalog3Vo{
        private String catalog2Id;
        private String id;
        private String name;
    }
}
