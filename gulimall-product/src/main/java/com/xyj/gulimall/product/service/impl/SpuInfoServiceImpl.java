package com.xyj.gulimall.product.service.impl;

import com.xyj.common.constant.ProductConstant;
import com.xyj.common.to.SkuHasStockTo;
import com.xyj.common.to.SkuReductionTo;
import com.xyj.common.to.SpuBoundsTo;
import com.xyj.common.to.es.SkuEsModel;
import com.xyj.common.utils.R;
import com.xyj.gulimall.product.entity.*;
import com.xyj.gulimall.product.openfeign.coupon.CouponFeignService;
import com.xyj.gulimall.product.openfeign.search.SearchFeignService;
import com.xyj.gulimall.product.openfeign.ware.WareFeignService;
import com.xyj.gulimall.product.service.*;
import com.xyj.gulimall.product.vo.SpuSaveVo;
import com.xyj.gulimall.product.vo.spu.save.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xyj.common.utils.PageUtils;
import com.xyj.common.utils.Query;

import com.xyj.gulimall.product.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {
    @Autowired
    SpuInfoDescService spuInfoDescService;
    @Autowired
    SpuImagesService spuImagesService;
    @Autowired
    AttrService attrService;
    @Autowired
    ProductAttrValueService productAttrValueService;
    @Autowired
    SkuInfoService skuInfoService;
    @Autowired
    SkuImagesService skuImagesService;
    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;
    @Autowired
    CouponFeignService couponFeignService;
    @Autowired
    BrandService brandService;
    @Autowired
    SpuInfoService spuInfoService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    WareFeignService wareFeignService;
    @Autowired
    SearchFeignService searchFeignService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * TODO 高级部分继续完善
     *
     * @param spuSaveVo
     */
    @Transactional
    @Override
    public void saveSpuInfo(SpuSaveVo spuSaveVo) {
        // 1. 保存spu基本信息 pms_spu_info
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(spuSaveVo, spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        this.saveBaseSpuInfo(spuInfoEntity);// mybatis自动帮我们返回自增id
        System.out.println(spuInfoEntity);
        // 2. 保存spu的描述图片 pms_spu_info_desc
        List<String> decript = spuSaveVo.getDecript();
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        spuInfoDescEntity.setSpuId(spuInfoEntity.getId());
        spuInfoDescEntity.setDecript(String.join(",", decript));
        spuInfoDescService.saveSpuInfoDescript(spuInfoDescEntity);
        // 3. 保存spu的图片集 pms_spu_images
        Long spuId = spuInfoEntity.getId();
        List<String> images = spuSaveVo.getImages();
        spuImagesService.saveSpuImages(spuId, images);
        // 4. 保存spu的规格参数 pms_product_attr_value
        List<BaseAttrs> baseAttrs = spuSaveVo.getBaseAttrs();
        List<ProductAttrValueEntity> collect = baseAttrs.stream().map((baseAttr) -> {
            ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
            productAttrValueEntity.setAttrId(baseAttr.getAttrId());
            productAttrValueEntity.setAttrValue(baseAttr.getAttrValues());
            productAttrValueEntity.setSpuId(spuInfoEntity.getId());
            productAttrValueEntity.setQuickShow(baseAttr.getShowDesc());
            AttrEntity attrEntity = attrService.getById(baseAttr.getAttrId());
            String attrName = attrEntity.getAttrName();
            productAttrValueEntity.setAttrName(attrName);
            return productAttrValueEntity;
        }).collect(Collectors.toList());
        productAttrValueService.saveProductAttrValue(collect);
        // 6.保存spu的积分信息 gulimall_sms->spu_bounds
        Bounds bounds = spuSaveVo.getBounds();
        SpuBoundsTo spuBoundsTo = new SpuBoundsTo();
        BeanUtils.copyProperties(bounds, spuBoundsTo);
        spuBoundsTo.setSpuId(spuInfoEntity.getId());
        R r = couponFeignService.saveSpuBounds(spuBoundsTo);
        if (r.getCode() != 0) {
            log.error("远程保存spu积分信息失败!");
        }
        // 5. 保存spu对应的所有sku信息
        List<Skus> skus = spuSaveVo.getSkus();
        if (skus != null && skus.size() != 0) {
            // 5.1 sku基本信息：sku_info
            skus.forEach((sku) -> {
                String defaultImg = "";
                for (Images img : sku.getImages()) {
                    if (img.getDefaultImg() == 1) {
                        defaultImg = img.getImgUrl();
                    }
                }
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(sku, skuInfoEntity);
                skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
                skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSpuId(spuInfoEntity.getId());
                skuInfoEntity.setSkuDefaultImg(defaultImg);
                skuInfoService.saveSkuInfo(skuInfoEntity);
                // 5.2 sku的图片信息 sku_images
                List<SkuImagesEntity> sku_images = sku.getImages().stream().map(img -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuInfoEntity.getSkuId());
                    skuImagesEntity.setDefaultImg(img.getDefaultImg());
                    skuImagesEntity.setImgUrl(img.getImgUrl());
                    return skuImagesEntity;
                }).filter(skuImagesEntity -> StringUtils.isNotEmpty(skuImagesEntity.getImgUrl())).collect(Collectors.toList());


                skuImagesService.saveBatch(sku_images);
                // 5.3 sku的销售属性信息 sku_sale_attr_value
                List<Attr> attrs = sku.getAttr();
                List<SkuSaleAttrValueEntity> skuSale = attrs.stream().map(attr -> {
                    SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(attr, skuSaleAttrValueEntity);
                    skuSaleAttrValueEntity.setSkuId(skuInfoEntity.getSkuId());
                    return skuSaleAttrValueEntity;
                }).collect(Collectors.toList());

                skuSaleAttrValueService.saveBatch(skuSale);
                // 5.4 sku的优惠满减信息 gulimall_sms->sms_sku_full_reduction\sms_member _price
                SkuReductionTo skuReductionTo = new SkuReductionTo();// 服务之间调用 需要利用TO对象进行传输 因为要转换为JSON
                BeanUtils.copyProperties(sku, skuReductionTo);
                skuReductionTo.setSkuId(skuInfoEntity.getSkuId());
                if (skuReductionTo.getFullCount() > 0 || skuReductionTo.getFullPrice().compareTo(new BigDecimal("0")) > 0) {
                    R r3 = couponFeignService.saveSkuReduction(skuReductionTo);
                    if (r3.getCode() != 0) {
                        log.error("远程保存优惠信息失败!");
                    }
                }
            });
        }

    }

    @Override
    public void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity) {
        this.baseMapper.insert(spuInfoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SpuInfoEntity> queryWrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        String status = (String) params.get("status");
        String brandId = (String) params.get("brandId");
        String catelogId = (String) params.get("catelogId");
        if (StringUtils.isNotEmpty(key)) {
            queryWrapper.and(w -> {
                w.eq("id", key).or().like("spu_name", key);
            });
        }
        if (StringUtils.isNotEmpty(status)) {
            queryWrapper.and(w -> {
                w.eq("publish_status", status);
            });
        }
        if (StringUtils.isNotEmpty(brandId) && !"0".equalsIgnoreCase(brandId)) {
            queryWrapper.and(w -> {
                w.eq("brand_id", brandId);
            });
        }
        if (StringUtils.isNotEmpty(catelogId) && !"0".equalsIgnoreCase(catelogId)) {
            queryWrapper.and(w -> {
                // 大坑 `pms_spu_info`表中的商品分类id字段为catalog_id  设计的时候写错了！
                w.eq("catalog_id", catelogId);
            });
        }
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                queryWrapper
        );
        return new PageUtils(page);
    }

    @Override
    public void spuUp(Long spuId) {
        List<SkuEsModel> upProducts = new ArrayList<>();
        // 1.查出当前spuid所对应的所有的sku信息，品牌名字
        List<SkuInfoEntity> skuInfoEntities = skuInfoService.getSkusBySpuId(spuId);
        // 查出当前sku的属性
        List<ProductAttrValueEntity> baseListForSpu = productAttrValueService.getBaseListForSpu(spuId);
        List<Long> attrIds = baseListForSpu.stream().map(ProductAttrValueEntity::getAttrId).collect(Collectors.toList());

        List<Long> selectAttrsIds = attrService.selectSearchAttrs(attrIds);
        Set<Long> attrIdsSet = new HashSet<>(selectAttrsIds);

        // 查到的selectAttrsIds是否在baseListForSpu 里面 因为只需要search_type为1的可检索的
        List<SkuEsModel.Attrs> attrs = baseListForSpu.stream().filter(item -> attrIdsSet.contains(item.getAttrId()))
                .map(item -> {
                    SkuEsModel.Attrs attrs1 = new SkuEsModel.Attrs();
                    BeanUtils.copyProperties(item, attrs1);
                    return attrs1;
                }).collect(Collectors.toList());
        // 查询sku是否有库存
        Map<Long, Boolean> hasStockBooleanMap = null;
        List<Long> skuIdList = skuInfoEntities.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());
        try {
            R skuHasStock = wareFeignService.getSkuHasStock(skuIdList);
            List<SkuHasStockTo> skuHasStockTos = (List<SkuHasStockTo>) skuHasStock.get("data");
            hasStockBooleanMap = skuHasStockTos.stream().collect(Collectors.toMap(SkuHasStockTo::getSkuId, SkuHasStockTo::getHasStock));
        }catch (Exception e){
            log.error("查询库存失败");
            log.error(e.getMessage());
        }
        // 2.封装每一个sku的信息
        Map<Long, Boolean> finalHasStockBooleanMap = hasStockBooleanMap;
        List<SkuEsModel> skuEsModels = skuInfoEntities.stream().map((sku) -> {
            SkuEsModel skuEsModel = new SkuEsModel();
            BeanUtils.copyProperties(sku, skuEsModel);
            skuEsModel.setSkuPrice(sku.getPrice());
            skuEsModel.setSkuImg(sku.getSkuDefaultImg());
            // 发送远程调用，查询是否有库存
            if(finalHasStockBooleanMap == null){
                skuEsModel.setHasStock(true);
            }else{
                Boolean hasStockForSkuId = finalHasStockBooleanMap.get(sku.getSkuId());
                skuEsModel.setHasStock(hasStockForSkuId);
            }
            // 热度评分 0
            skuEsModel.setHotScore(0L);
            // 查询品牌和品牌名称
            BrandEntity brandEntity = brandService.getById(skuEsModel.getBrandId());
            skuEsModel.setBrandName(brandEntity.getName());
            skuEsModel.setBrandImg(brandEntity.getLogo());
            CategoryEntity categoryEntity = categoryService.getById(skuEsModel.getCatalogId());
            skuEsModel.setCatalogName(categoryEntity.getName());
            // attr填充
            skuEsModel.setAttrs(attrs);

            return skuEsModel;
        }).collect(Collectors.toList());

        // 数据发送给ES保存
        R r = searchFeignService.productStatusUp(skuEsModels);
        if(r.getCode() == 0){
            log.debug("远程调用成功");
            // 修改上架状态
            this.skuInfoService.updateSpuStatus(spuId, ProductConstant.Status.SPU_UP.getCode());
        }else {
            log.debug("远程调用失败");
            // TODO 重复调用？ 接口幂等性; 重试机制
            // Feign的调用流程
            // 1 构造请求数据，对象转为json
            // 2 发送请求
        }

    }

    @Override
    public SpuInfoEntity getSpuInfoBySkuId(Long id) {
        SkuInfoEntity skuInfoEntity = skuInfoService.getById(id);
        Long spuId = skuInfoEntity.getSpuId();
        SpuInfoEntity spuInfoEntity = spuInfoService.getById(spuId);
        return spuInfoEntity;
    }
}