package com.xyj.gulimall.ware.service.impl;

import com.xyj.common.constant.WareConstant;
import com.xyj.gulimall.ware.dao.PurchaseDetailDao;
import com.xyj.gulimall.ware.entity.PurchaseDetailEntity;
import com.xyj.gulimall.ware.service.PurchaseDetailService;
import com.xyj.gulimall.ware.service.WareSkuService;
import com.xyj.gulimall.ware.vo.MergeVo;
import com.xyj.gulimall.ware.vo.PurchaseDoneVo;
import com.xyj.gulimall.ware.vo.PurchaseItemDoneVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xyj.common.utils.PageUtils;
import com.xyj.common.utils.Query;

import com.xyj.gulimall.ware.dao.PurchaseDao;
import com.xyj.gulimall.ware.entity.PurchaseEntity;
import com.xyj.gulimall.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {
    @Autowired
    PurchaseDetailService purchaseDetailService;

    @Autowired
    WareSkuService wareSkuService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryUnReceiveList(Map<String, Object> params) {
        QueryWrapper<PurchaseEntity> purchaseEntityQueryWrapper = new QueryWrapper<>();
        purchaseEntityQueryWrapper.in("status", Arrays.asList(0, 1));
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                purchaseEntityQueryWrapper
        );
        return new PageUtils(page);

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void purchaseMerge(MergeVo mergeVo) {
        Long purchaseId = mergeVo.getPurchaseId();
        if (purchaseId == null) {
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setUpdateTime(new Date());
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.CREATED.getCode());
            this.save(purchaseEntity);
            purchaseId = purchaseEntity.getId(); // 新建
        }
        //  确认采购单状态是0 或者 1才可以合并
        List<Long> items = mergeVo.getItems();
        Long finalPurchaseId = purchaseId;
        List<PurchaseDetailEntity> collect = items.stream().map(i -> {
            PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
            purchaseDetailEntity.setId(i);
            purchaseDetailEntity.setPurchaseId(finalPurchaseId);
            purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode());
            return purchaseDetailEntity;
        }).collect(Collectors.toList());
        purchaseDetailService.updateBatchById(collect);
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(purchaseId);
        purchaseEntity.setUpdateTime(new Date());
        this.updateById(purchaseEntity);
    }

    /**
     * 领取采购单
     *
     * @param ids 采购单的id
     */
    @Transactional
    @Override
    public void received(List<Long> ids) {
        //1、确认当前采购单是新建或者是已分配状态
        List<PurchaseEntity> collect = ids.stream().map(this::getById).filter(item -> Objects.equals(item.getStatus(), WareConstant.PurchaseStatusEnum.CREATED.getCode()) ||
                Objects.equals(item.getStatus(), WareConstant.PurchaseStatusEnum.ASSIGNED.getCode())).peek(item -> {
            //改变完状态的采购单 peek执行操作 不需要返回值
            item.setStatus(WareConstant.PurchaseStatusEnum.RECEIVE.getCode());
            item.setUpdateTime(new Date());
        }).collect(Collectors.toList());
        //2、改变采购单的状态
        this.updateBatchById(collect);
        //3、改变采购项的状态
        collect.forEach((item) -> {
            List<PurchaseDetailEntity> list = purchaseDetailService.listDetailByPurchaseId(item.getId());
            List<PurchaseDetailEntity> detailEntities = list.stream().map(entity -> {
                PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
                purchaseDetailEntity.setId(entity.getId());
                purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.BUYING.getCode());
                return purchaseDetailEntity;
            }).collect(Collectors.toList());
            purchaseDetailService.updateBatchById(detailEntities);
        });
    }

    /**
     * 完成采购单
     *
     * @param doneVo
     */
    @Override
    public void done(PurchaseDoneVo doneVo) {

        Long id = doneVo.getId();

        //1、改变采购项的状态
        boolean flag = true;
        List<PurchaseItemDoneVo> items = doneVo.getItems();
        List<PurchaseDetailEntity> updates = new ArrayList<>();
        for (PurchaseItemDoneVo item : items) {
            PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
            if (Objects.equals(item.getStatus(), WareConstant.PurchaseDetailStatusEnum.HASERROR.getCode())) {
                flag = false;
                purchaseDetailEntity.setStatus(item.getStatus());
            } else {
                purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.FINISH.getCode());
                //3、将成功采购的进行入库
                //查出当前采购项的详细信息
                //PurchaseDetailEntity entity = purchaseDetailService.getById(item.getItemId());
                PurchaseDetailEntity entity = purchaseDetailService.getById(item.getItemId());
                wareSkuService.addStock(entity.getSkuId(), entity.getWareId(), entity.getSkuNum());
            }
            purchaseDetailEntity.setId(item.getItemId());

            updates.add(purchaseDetailEntity);
        }
        //批量更新采购项
        purchaseDetailService.updateBatchById(updates);
        //2、改变采购单状态
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(id);
        purchaseEntity.setStatus(flag ? WareConstant.PurchaseStatusEnum.FINISH.getCode() : WareConstant.PurchaseStatusEnum.HASERROR.getCode());
        purchaseEntity.setUpdateTime(new Date());
        this.updateById(purchaseEntity);

    }
}