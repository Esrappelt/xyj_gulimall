package com.xyj.gulimall.product.service.impl;

import com.xyj.gulimall.product.entity.AttrEntity;
import com.xyj.gulimall.product.service.AttrService;
import com.xyj.gulimall.product.vo.AttrGroupWithAttrsVo;
import com.xyj.gulimall.product.vo.SpuItemAttrGroupVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xyj.common.utils.PageUtils;
import com.xyj.common.utils.Query;

import com.xyj.gulimall.product.dao.AttrGroupDao;
import com.xyj.gulimall.product.entity.AttrGroupEntity;
import com.xyj.gulimall.product.service.AttrGroupService;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    private AttrService attrService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );
        return new PageUtils(page);
    }
    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {
        String key = (String) params.get("key");
        // select * from pms_attr_group where catlog_id = ? and (attr_group_id=key or attr_group_name like %key%)
        QueryWrapper<AttrGroupEntity> queryWrapper = new QueryWrapper<AttrGroupEntity>();
        // 如果key存在 且不为空 那么就有模糊查询
        if (key != null && key.length() != 0) {
            queryWrapper.eq("attr_group_id", key).or().like("attr_group_name", key);
        }
        //
        if (catelogId != 0) {
            // 在这里添加catelogId的模糊查询功能
            queryWrapper.eq("catelog_id", catelogId);
        }
        IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params),
                queryWrapper);
        return new PageUtils(page);
    }

    /**
     * 根据商品分类的id 查出所有的规格参数
     * @param catelogId
     * @return
     */
    @Override
    public List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCateLogId(Long catelogId) {
        List<AttrGroupEntity> attrGroupEntities = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
        return attrGroupEntities.stream().map((attrGroup) -> {
            AttrGroupWithAttrsVo attrGroupWithAttrsVo = new AttrGroupWithAttrsVo();
            BeanUtils.copyProperties(attrGroup, attrGroupWithAttrsVo);
            List<AttrEntity> attrs = attrService.getAttrRelation(attrGroup.getAttrGroupId());
            attrGroupWithAttrsVo.setAttrs(attrs);
            return attrGroupWithAttrsVo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<SpuItemAttrGroupVo> getAttrGroupWithAttrsBySpuId(Long spuId, Long catalogId) {
        return this.baseMapper.getAttrGroupWithAttrsBySpuId(spuId, catalogId);
    }

}