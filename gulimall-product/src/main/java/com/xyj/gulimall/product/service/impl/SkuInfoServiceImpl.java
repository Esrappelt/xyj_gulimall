package com.xyj.gulimall.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.xyj.common.utils.R;
import com.xyj.gulimall.product.entity.SkuImagesEntity;
import com.xyj.gulimall.product.entity.SpuInfoDescEntity;
import com.xyj.gulimall.product.openfeign.seckill.SeckillFeignService;
import com.xyj.gulimall.product.service.*;
import com.xyj.gulimall.product.vo.SeckillInfovo;
import com.xyj.gulimall.product.vo.SkuItemSaleAttrVo;
import com.xyj.gulimall.product.vo.SkuItemVo;
import com.xyj.gulimall.product.vo.SpuItemAttrGroupVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xyj.common.utils.PageUtils;
import com.xyj.common.utils.Query;

import com.xyj.gulimall.product.dao.SkuInfoDao;
import com.xyj.gulimall.product.entity.SkuInfoEntity;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Autowired
    SkuImagesService skuImagesService;
    @Autowired
    SpuInfoDescService spuInfoDescService;
    @Autowired
    AttrGroupService attrGroupService;
    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;
    @Autowired
    SeckillFeignService seckillFeignService;

    @Autowired
    ThreadPoolExecutor executor;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuInfo(SkuInfoEntity skuInfoEntity) {
        this.save(skuInfoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SkuInfoEntity> queryWrapper = new QueryWrapper<>();

        String key = (String) params.get("key");
        String brandId = (String) params.get("brandId");
        String catelogId = (String) params.get("catelogId");
        String min = (String) params.get("min");
        String max = (String) params.get("max");
        if (StringUtils.isNotEmpty(key)) {
            queryWrapper.and(w -> {
                w.eq("sku_id", key).or().like("spu_name", key);
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
        // 参数校验
        try {
            BigDecimal _min = new BigDecimal(min);
            BigDecimal _max = new BigDecimal(max);
            BigDecimal zero = BigDecimal.ZERO;
            if (_min.compareTo(zero) <= 0) {
                _min = zero;
            }
            if (_max.compareTo(zero) <= 0) {
                _max = zero;
            }
            assert _max.compareTo(_min) >= 0;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        if (StringUtils.isNotEmpty(min)) {
            queryWrapper.and(w -> {
                // 大坑 `pms_spu_info`表中的商品分类id字段为catalog_id  设计的时候写错了！
                w.ge("price", min);
            });
        }
        if (StringUtils.isNotEmpty(max)) {
            try {
                BigDecimal bigDecimal = new BigDecimal(max);
                if (bigDecimal.compareTo(BigDecimal.ZERO) > 0) {
                    queryWrapper.and(w -> {
                        // 大坑 `pms_spu_info`表中的商品分类id字段为catalog_id  设计的时候写错了！
                        w.le("price", max);
                    });
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        }

        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                queryWrapper
        );
        return new PageUtils(page);
    }

    @Override
    public List<SkuInfoEntity> getSkusBySpuId(Long spuId) {
        return this.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));
    }

    @Override
    public void updateSpuStatus(Long spuId, Integer code) {
        this.baseMapper.updateStatus(spuId, code);
    }

    @Override
    public SkuItemVo getSkuItem(Long skuId) {

        // 异步编排
        SkuItemVo skuItemVo = new SkuItemVo();
        // 1.sku基本信息获取 pms_sku_info

        CompletableFuture<SkuInfoEntity> infoFuture = CompletableFuture.supplyAsync(() -> {
            SkuInfoEntity skuInfoEntity = getById(skuId);
            skuItemVo.setInfo(skuInfoEntity);
            return skuInfoEntity;
        }, executor);

        CompletableFuture<Void> saleAttrFuture = infoFuture.thenAcceptAsync((skuInfoEntity) -> {
            //没有返回结果
            // 3.spu的销售属性
            List<SkuItemSaleAttrVo> saleAttrVos = skuSaleAttrValueService.getSaleAttrsBySpuId(skuInfoEntity.getSpuId());
            skuItemVo.setSaleAttr(saleAttrVos);
        }, executor);

        CompletableFuture<Void> infoDescFuture = infoFuture.thenAcceptAsync((skuInfoEntity) -> {
            // 4.spu的介绍psm_spu_info_desc
            SpuInfoDescEntity spuInfoDescEntity = spuInfoDescService.getById(skuInfoEntity.getSpuId());
            skuItemVo.setDesc(spuInfoDescEntity);
        }, executor);

        CompletableFuture<Void> baseAttrFuture = infoFuture.thenAcceptAsync((skuInfoEntity) -> {
            // 5.spu的规格参数信息
            List<SpuItemAttrGroupVo> attrGroupVos = attrGroupService.getAttrGroupWithAttrsBySpuId(skuInfoEntity.getSpuId(), skuInfoEntity.getCatalogId());
            skuItemVo.setGroupAttrs(attrGroupVos);
        });

        CompletableFuture<Void> imageFuture = CompletableFuture.runAsync(() -> {
            // 2.sku图片信息psm_sku_images
            List<SkuImagesEntity> skuImagesEntities = skuImagesService.getImagesBySkuId(skuId);
            skuItemVo.setImages(skuImagesEntities);
        }, executor);

        CompletableFuture<Void> seckillFuture = CompletableFuture.runAsync(() -> {
            // 查询当前sku是否参与秒杀优惠
            R r = seckillFeignService.getSkuSeckillInfo(skuId);
            if (r.getCode() == 0) {
                SeckillInfovo seckillInfovo = r.getData(new TypeReference<SeckillInfovo>() {
                });
                skuItemVo.setSeckillInfo(seckillInfovo);
            }
        }, executor);
        try {
            CompletableFuture.allOf(saleAttrFuture, infoDescFuture, baseAttrFuture, imageFuture, seckillFuture).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return skuItemVo;
    }
}