package com.xyj.gulimall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.xyj.common.utils.R;
import com.xyj.gulimall.ware.openfeign.MemberFeignService;
import com.xyj.gulimall.ware.vo.FareVo;
import com.xyj.gulimall.ware.vo.MemberAddressVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xyj.common.utils.PageUtils;
import com.xyj.common.utils.Query;

import com.xyj.gulimall.ware.dao.WareInfoDao;
import com.xyj.gulimall.ware.entity.WareInfoEntity;
import com.xyj.gulimall.ware.service.WareInfoService;


@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {

    @Autowired
    MemberFeignService memberFeignService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),
                new QueryWrapper<WareInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public FareVo getFare(Long addrId) {
        FareVo fareVo = new FareVo();
        R info = memberFeignService.info(addrId);
        if(info.getCode() == 0){
            MemberAddressVo addressVo = info.getData("memberReceiveAddress", new TypeReference<MemberAddressVo>() {
            });
            if(addressVo != null){
                String phone = addressVo.getPhone();
                BigDecimal fare = new BigDecimal(phone.substring(phone.length() - 1));
                fareVo.setAddress(addressVo);
                fareVo.setFare(fare);
            }
        }
        // 没有收货地址
        return fareVo;
    }

}