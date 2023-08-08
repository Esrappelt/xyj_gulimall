package com.xyj.gulimal.gulimallsearch.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @Author jie
 * @Date 2023/7/28 14:33
 * 封装页面所有的查询条件
 */
@Data
@ToString
public class SearchParam {
    private String keyword;//关键字
    private Long catalog3Id;// 三级分类Id
    /**
     * sort=saleCount_asc/desc
     * sort=skuPrice_asc/desc
     * sort=hotScore_asc/desc
     */
    private String sort; //排序条件 枚举类
    /**
     * 过滤条件
     * hasStock=0/1
     * skuPrice=[min,max] 1_500/_500/1_
     * brandId=[1,2,3]
     * catalog3Id=[1,2,3]
     * attrs=2_3寸:4寸
     */
    private Integer hasStock;//是否显示有货
    private String skuPrice;//价格区间查询
    private List<Long> brandId;//品牌Id查询
    private List<String> attrs;//按照属性查询
    private Integer pageNum;//页码
}
