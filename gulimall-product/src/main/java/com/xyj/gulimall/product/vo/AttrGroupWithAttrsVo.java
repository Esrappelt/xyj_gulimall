package com.xyj.gulimall.product.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.xyj.gulimall.product.entity.AttrEntity;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @Author jie}
 * @Date 2023/6/21 21:01}
 */
@Data
@ToString
public class AttrGroupWithAttrsVo {
    private Long attrGroupId;
    /**
     * 组名
     */
    private String attrGroupName;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 描述
     */
    private String descript;
    /**
     * 组图标
     */
    private String icon;
    /**
     * 所属分类id
     */
    private Long catelogId;

    private List<AttrEntity> attrs;
}
