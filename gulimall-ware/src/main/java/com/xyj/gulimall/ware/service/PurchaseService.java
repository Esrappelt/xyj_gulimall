package com.xyj.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xyj.common.utils.PageUtils;
import com.xyj.gulimall.ware.entity.PurchaseEntity;
import com.xyj.gulimall.ware.vo.MergeVo;
import com.xyj.gulimall.ware.vo.PurchaseDoneVo;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author xyj
 * @email xyj@gmail.com
 * @date 2023-06-15 18:36:07
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryUnReceiveList(Map<String, Object> params);

    void purchaseMerge(MergeVo mergeVo);

    void received(List<Long> mergeVo);

    void done(PurchaseDoneVo doneVo);
}

