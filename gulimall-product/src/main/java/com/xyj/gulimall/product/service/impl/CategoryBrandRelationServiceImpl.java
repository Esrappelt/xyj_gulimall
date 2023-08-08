package com.xyj.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.xyj.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.xyj.gulimall.product.dao.BrandDao;
import com.xyj.gulimall.product.dao.CategoryDao;
import com.xyj.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.xyj.gulimall.product.entity.BrandEntity;
import com.xyj.gulimall.product.entity.CategoryEntity;
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

import com.xyj.gulimall.product.dao.CategoryBrandRelationDao;
import com.xyj.gulimall.product.entity.CategoryBrandRelationEntity;
import com.xyj.gulimall.product.service.CategoryBrandRelationService;
import org.springframework.transaction.annotation.Transactional;


@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private BrandDao brandDao;

    @Autowired
    private CategoryBrandRelationDao categoryBrandRelationDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                new QueryWrapper<CategoryBrandRelationEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 冗余字段也得级联更新 brandName和categoryName
     * 需要开启事务
     */
    @Transactional
    @Override
    public void saveDetail(CategoryBrandRelationEntity categoryBrandRelation) {
        Long brandId = categoryBrandRelation.getBrandId();
        Long catelogId = categoryBrandRelation.getCatelogId();
        CategoryEntity categoryEntity = categoryDao.selectById(catelogId);
        BrandEntity brandEntity = brandDao.selectById(brandId);
        String categoryEntityName = categoryEntity.getName();
        String brandEntityName = brandEntity.getName();
        categoryBrandRelation.setBrandName(brandEntityName);
        categoryBrandRelation.setCatelogName(categoryEntityName);
        this.save(categoryBrandRelation);
    }

    @Override
    public void updateBrand(Long brandId, String brandName) {
        // 设置品牌的名字
        CategoryBrandRelationEntity categoryBrandRelationEntity = new CategoryBrandRelationEntity();
        categoryBrandRelationEntity.setBrandId(brandId);
        categoryBrandRelationEntity.setBrandName(brandName);
        // 查询语句的编写 where brand_id = ?
        UpdateWrapper<CategoryBrandRelationEntity> updateWrapper = new UpdateWrapper<CategoryBrandRelationEntity>().eq("brand_id", brandId);
        // 更新品牌名 update xxx set brand_name= ? where brand_id = ?
        this.update(categoryBrandRelationEntity, updateWrapper);
    }

    @Override
    public void updateCategory(Long catId, String name) {
        this.baseMapper.updateCategory(catId, name);
    }

    @Override
    public List<BrandEntity> getBrandsByCatId(Long catId) {
        List<CategoryBrandRelationEntity> relationEntities = categoryBrandRelationDao.selectList(new QueryWrapper<CategoryBrandRelationEntity>().eq("catelog_id", catId));
        return relationEntities.stream().map((relationEntity) -> {
            Long brandId = relationEntity.getBrandId();
            return brandDao.selectById(brandId);
        }).collect(Collectors.toList());
    }
}


















