package com.xyj.gulimall.product.service.impl;

import com.xyj.gulimall.product.service.CategoryBrandRelationService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xyj.common.utils.PageUtils;
import com.xyj.common.utils.Query;

import com.xyj.gulimall.product.dao.BrandDao;
import com.xyj.gulimall.product.entity.BrandEntity;
import com.xyj.gulimall.product.service.BrandService;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<BrandEntity> queryWrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (key != null && key.length() != 0) {
            queryWrapper.eq("brand_id", key)
                    .or()
                    .like("name", key)
                    .or()
                    .like("descript", key).
                    or()
                    .like("first_letter", key);
            IPage<BrandEntity> page = this.page(
                    new Query<BrandEntity>().getPage(params),
                    queryWrapper
            );
            return new PageUtils(page);
        } else {
            return new PageUtils(this.page(
                    new Query<BrandEntity>().getPage(params),
                    new QueryWrapper<>()
            ));
        }
    }

    /**
     * 更新的时候 将冗余字段进行级联更新！
     * @param brand
     */
    @Override
    public void updateDetail(BrandEntity brand) {
        // 更新时先将自己的字段进行更新
        this.updateById(brand);
        // 将关联表CategoryBrandRelation中的brandName进行级联更新
        String brandName = brand.getName();
        Long brandId = brand.getBrandId();
        // 如果品牌名字不为空， 则需要更新
        if(StringUtils.isNotEmpty(brandName)){
            // 操作关联表 使用对应的service
            categoryBrandRelationService.updateBrand(brandId, brandName);
            // TODO 更新其他关联操作
        }
    }

}