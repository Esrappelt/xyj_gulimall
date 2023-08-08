package com.xyj.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xyj.common.utils.PageUtils;
import com.xyj.gulimall.ware.entity.WareInfoEntity;
import com.xyj.gulimall.ware.vo.FareVo;

import java.util.Map;

/**
 * 仓库信息
 *
 * @author xyj
 * @email xyj@gmail.com
 * @date 2023-06-15 18:36:06
 */
public interface WareInfoService extends IService<WareInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 根据收货地址计算运费
     *
     * @param addrId
     * @return
     */
    FareVo getFare(Long addrId);
}

