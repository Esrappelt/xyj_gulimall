package com.xyj.gulimal.gulimallsearch.vo;

import com.xyj.common.to.es.SkuEsModel;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @Author jie
 * @Date 2023/7/28 14:53
 */


/**
 * 商品检索的结果
 */
@Data
@ToString
public class SearchResult {
    // 查询到的商品信息
    private List<SkuEsModel> products;
    //当前页码,给个默认值
    private Integer pageNum = 1;
    //总记录数
    private Long total;
    // 总页码
    private Integer totalPages;
    // 导航页
    private List<Integer> pageNavs;
    // 商品所关联的品牌信息
    private List<BrandVo> brands;
    // 品牌的属性信息
    private List<AttrVo> attrs;
    //所涉及的所有分类
    private List<CatalogVo> catalogs;


    @Data
    @ToString
    public static class BrandVo {
        private Long brandId;
        private String brandName;
        private String brandImg;
    }

    @Data
    @ToString
    public static class AttrVo {
        private Long attrId;
        private String attrName;
        private List<String> attrValue;
    }

    @Data
    @ToString
    public static class CatalogVo {
        private Long catalogId;
        private String catalogName;
    }



}
