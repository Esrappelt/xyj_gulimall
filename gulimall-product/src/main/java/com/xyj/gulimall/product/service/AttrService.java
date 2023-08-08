package com.xyj.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xyj.common.utils.PageUtils;
import com.xyj.gulimall.product.entity.AttrEntity;
import com.xyj.gulimall.product.vo.AttrGroupRelationVo;
import com.xyj.gulimall.product.vo.AttrRespVo;
import com.xyj.gulimall.product.vo.AttrVo;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author xyj
 * @email xyj@gmail.com
 * @date 2023-06-19 20:55:29
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveAttr(AttrVo attrVo);

    PageUtils queryBaseAttrList(Map<String, Object> params, Long catelogId, String attrType);

    AttrRespVo queryAttrInfo(Long attrId);

    void updateAttr(AttrVo attrVo);

    List<AttrEntity> getAttrRelation(Long attrGroupId);

    void deleteRelation(AttrGroupRelationVo... attrGroupRelationVos);

    PageUtils getNoRelationAttr(Long attrgroupId, Map<String, Object> params);

    List<Long> selectSearchAttrs(List<Long> attrIds);
}

