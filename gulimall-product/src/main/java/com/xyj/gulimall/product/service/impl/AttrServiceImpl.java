package com.xyj.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xyj.common.constant.ProductConstant;
import com.xyj.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.xyj.gulimall.product.dao.AttrGroupDao;
import com.xyj.gulimall.product.dao.CategoryDao;
import com.xyj.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.xyj.gulimall.product.entity.AttrGroupEntity;
import com.xyj.gulimall.product.entity.CategoryEntity;
import com.xyj.gulimall.product.service.CategoryService;
import com.xyj.gulimall.product.vo.AttrGroupRelationVo;
import com.xyj.gulimall.product.vo.AttrRespVo;
import com.xyj.gulimall.product.vo.AttrVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xyj.common.utils.PageUtils;
import com.xyj.common.utils.Query;

import com.xyj.gulimall.product.dao.AttrDao;
import com.xyj.gulimall.product.entity.AttrEntity;
import com.xyj.gulimall.product.service.AttrService;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    AttrAttrgroupRelationDao attrAttrgroupRelationDao;

    @Autowired
    AttrGroupDao attrGroupDao;

    @Autowired
    CategoryDao categoryDao;

    @Autowired
    CategoryService categoryService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveAttr(AttrVo attrVo) {
        AttrEntity attrEntity = new AttrEntity();
        // 将attrvo里面的值 复制到attrEntity里面 浅拷贝 是有关联的
        BeanUtils.copyProperties(attrVo, attrEntity);
        // po保存到数据库
        this.save(attrEntity);
        // 有两个类型 一个是基本属性 一个是销售属性 必须判断
        if (Objects.equals(ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode(), attrVo.getAttrType())) {
            // 级联更新 将他放到 属性与属性分组 的关联表
            // 关联表一般就是两个表的id 放在一起作为主键
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            attrAttrgroupRelationEntity.setAttrGroupId(attrVo.getAttrGroupId());
            attrAttrgroupRelationEntity.setAttrId(attrEntity.getAttrId());
            // 保存到数据库
            attrAttrgroupRelationDao.insert(attrAttrgroupRelationEntity);
        }
    }

    public PageUtils queryBaseAttrList2(Map<String, Object> params, Long catelogId, String attrType) {
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper = queryWrapper.eq("attr_type", "base".equalsIgnoreCase(attrType) ? 1 : 0);
        if (catelogId != 0) { // 假如我要以catelogId查询，则写这个
            log.debug("catelog_id:{}", catelogId);
            queryWrapper.eq("catelog_id", catelogId);
        }
        String key = (String) params.get("key");
        if (StringUtils.isNotEmpty(key)) {
            queryWrapper.and((wrapper) -> {
                wrapper.eq("attr_id", key).or().like("attr_name", key)
                ;
            });
        }
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                queryWrapper
        );
        PageUtils pageUtils = new PageUtils(page);
        List<AttrEntity> records = page.getRecords();
        // 结果的映射 用map!
        List<AttrRespVo> attrRespVos = records.stream().map((attrEntity) -> {
            AttrRespVo attrRespVo = new AttrRespVo();
            BeanUtils.copyProperties(attrEntity, attrRespVo);
            if ("base".equalsIgnoreCase(attrType)) { // 如果是基本属性 那么就不是销售 销售没有属性分组 这里需要判断
                Long attrId = attrEntity.getAttrId();
                if (attrId != null) {
                    AttrAttrgroupRelationEntity attrAttrgroupRelationEntity =
                            attrAttrgroupRelationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrId));
                    if (attrAttrgroupRelationEntity != null && attrAttrgroupRelationEntity.getAttrGroupId() != null) {
                        String attrGroupName = attrGroupDao.selectById(attrAttrgroupRelationEntity.getAttrGroupId()).getAttrGroupName();
                        attrRespVo.setGroupName(attrGroupName);
                    }
                }
            }
            // 这里一定要从attrEntity获取catelogId
            CategoryEntity categoryEntity = categoryDao.selectById(attrEntity.getCatelogId());
            if (categoryEntity != null) {
                String catelogName = categoryEntity.getName();
                attrRespVo.setCatelogName(catelogName);
            }
            return attrRespVo;
        }).collect(Collectors.toList());
        pageUtils.setList(attrRespVos);
        return pageUtils;
    }

    @Override
    public PageUtils queryBaseAttrList(Map<String, Object> params, Long catelogId, String attrType) {
        return queryBaseAttrList2(params, catelogId, attrType);
    }

    /**
     * 根据属性id 获取商品分类和属性分组
     *
     * @param attrId
     * @return AttrRespVo
     */
    @Override
    public AttrRespVo queryAttrInfo(Long attrId) {
        // 判断attrId字段
        if (attrId == null || attrId < 0L) {
            log.debug("异常:{}", attrId);
            throw new IllegalArgumentException("提供的attrId参数错误！");
        }
        // 1.根据attrid 得到属性实体类
        AttrEntity attrEntity = this.getById(attrId);
        AttrRespVo attrRespVo = new AttrRespVo();
        BeanUtils.copyProperties(attrEntity, attrRespVo);
        if (Objects.equals(attrEntity.getAttrType(), ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode())) {
            // 通过属性与属性分组关联表，得到属性分组id---attrGroupId
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity =
                    attrAttrgroupRelationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrId));
            if (attrAttrgroupRelationEntity != null) {
                Long attrGroupId = attrAttrgroupRelationEntity.getAttrGroupId();
                attrRespVo.setAttrGroupId(attrGroupId);
                AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrGroupId);
                if (attrGroupEntity != null) {
                    String attrGroupName = attrGroupEntity.getAttrGroupName();
                    // 设置vo
                    if (StringUtils.isNotEmpty(attrGroupName)) {
                        attrRespVo.setGroupName(attrGroupName);
                    }
                }
            }
        }
        // 通过AttrEntity得到catelogId，然后通过商品分类表获取商品名字
        Long catelogId = attrEntity.getCatelogId();
        if (catelogId != null) {
            log.debug("catelogId:{}", catelogId);
            CategoryEntity categoryEntity = categoryDao.selectById(catelogId);
            String catelog_name = categoryEntity.getName();
            if (StringUtils.isNotEmpty(catelog_name)) {
                attrRespVo.setCatelogName(catelog_name);
            }
            Long[] fileCateLogPath = categoryService.fileCateLogPath(catelogId);
            attrRespVo.setCatelogPath(fileCateLogPath);
        }
        return attrRespVo;
    }

    @Transactional
    @Override
    public void updateAttr(AttrVo attrVo) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attrVo, attrEntity);
        this.updateById(attrEntity);
        // 级联更新 relation 修改分组
        if (Objects.equals(ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode(), attrVo.getAttrType())) {
            long attrGroupId = attrVo.getAttrGroupId();
            Long attrId = attrVo.getAttrId();
            UpdateWrapper<AttrAttrgroupRelationEntity> updateWrapper = new UpdateWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrId);
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            attrAttrgroupRelationEntity.setAttrGroupId(attrGroupId);
            attrAttrgroupRelationDao.update(attrAttrgroupRelationEntity, updateWrapper);
        }
    }

    /**
     * 根据属性分组id  找到所有属于该分组的属性
     *
     * @param attrGroupId
     * @return
     */
    @Override
    public List<AttrEntity> getAttrRelation(Long attrGroupId) {
        // 获取所有属性
        QueryWrapper<AttrAttrgroupRelationEntity> entityQueryWrapper = new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", attrGroupId);
        List<AttrAttrgroupRelationEntity> relationEntities = attrAttrgroupRelationDao.selectList(entityQueryWrapper);
        List<Long> attrIds = relationEntities.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());
        if (attrIds.size() == 0) {
            return null;
        }
        Collection<AttrEntity> attrEntities = this.listByIds(attrIds);
        // 向下转型
        return (List<AttrEntity>) attrEntities;
    }

    @Override
    public void deleteRelation(AttrGroupRelationVo... attrGroupRelationVos) {
        List<AttrAttrgroupRelationEntity> relationEntityList = Arrays.stream(attrGroupRelationVos).map((attrGroupRelationVo) -> {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(attrGroupRelationVo, relationEntity);
            return relationEntity;
        }).collect(Collectors.toList());
        deleteRelationEntity(relationEntityList);
    }

    /**
     * 获取当前分组没有关联的属性
     *
     * @return
     */
    @Override
    public PageUtils getNoRelationAttr(Long attrgroupId, Map<String, Object> params) {
        // 1.当前分组只能关联自己所属商品下的所有属性 比如手机里面的分组 不可能去关联生物下面的属性
        Long catelogId = attrGroupDao.selectById(attrgroupId).getCatelogId();
        // 2. 当前分组只能关联别的分组没有关联的属性 因为属性分组和属性是一对一关系
        // 2.1 当前分类下的其他分组
        List<AttrGroupEntity> groupEntities = attrGroupDao.
                selectList(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
        // 2.2 这些分组所关联的属性
        List<Long> groupIdList = groupEntities.stream().map(AttrGroupEntity::getAttrGroupId).collect(Collectors.toList());
        List<AttrAttrgroupRelationEntity> attrGroupIdList = attrAttrgroupRelationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().in("attr_group_id", groupIdList));
        List<Long> attrIdList = attrGroupIdList.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<AttrEntity>()
                .eq("catelog_id", catelogId)
                .eq("attr_type", 1);// 基础属性 不是销售属性
        // 2.3 从当前分类的所有属性中移除这些属性
        if (attrIdList.size() > 0) {
            queryWrapper.notIn("attr_id", attrIdList);
        }
        String key = (String) params.get("key");
        if (StringUtils.isNotEmpty(key)) {
            queryWrapper.and((wrapper) -> {
                wrapper.eq("attr_id", key).or().like("attr_name", key);
            });
        }
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), queryWrapper);
        return new PageUtils(page);
    }

    @Override
    public List<Long> selectSearchAttrs(List<Long> attrIds) {

        return this.baseMapper.selectAttrIds(attrIds);
    }

    private void deleteRelationEntity(List<AttrAttrgroupRelationEntity> relationEntityList) {
        attrAttrgroupRelationDao.deleteRelationEntity(relationEntityList);
    }


}