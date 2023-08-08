package com.xyj.gulimall.ware.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xyj.common.utils.PageUtils;
import com.xyj.common.utils.Query;

import com.xyj.gulimall.ware.dao.PurchaseDetailDao;
import com.xyj.gulimall.ware.entity.PurchaseDetailEntity;
import com.xyj.gulimall.ware.service.PurchaseDetailService;


@Service("purchaseDetailService")
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailDao, PurchaseDetailEntity> implements PurchaseDetailService {
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<PurchaseDetailEntity> purchaseDetailEntityQueryWrapper = new QueryWrapper<>();

        String key = (String) params.get("key");

        if (StringUtils.isNotEmpty(key)) {
            purchaseDetailEntityQueryWrapper.and(w -> w.eq("purchase_id", key).or().eq("sku_id", key));
        }
        String wareId = (String) params.get("wareId");

        if (StringUtils.isNotEmpty(key)) {
            purchaseDetailEntityQueryWrapper.and(w -> w.eq("ware_id", wareId));
        }
        String status = (String) params.get("status");
        if (StringUtils.isNotEmpty(key)) {
            purchaseDetailEntityQueryWrapper.and(w -> w.eq("status", status));
        }

        IPage<PurchaseDetailEntity> page = this.page(
                new Query<PurchaseDetailEntity>().getPage(params),
                purchaseDetailEntityQueryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public List<PurchaseDetailEntity> listDetailByPurchaseId(Long id) {
        return this.list(new QueryWrapper<PurchaseDetailEntity>().eq("purchase_id", id));
    }
}