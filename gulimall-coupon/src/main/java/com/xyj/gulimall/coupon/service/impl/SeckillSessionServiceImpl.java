package com.xyj.gulimall.coupon.service.impl;

import com.xyj.gulimall.coupon.entity.SeckillSkuRelationEntity;
import com.xyj.gulimall.coupon.service.SeckillSkuRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xyj.common.utils.PageUtils;
import com.xyj.common.utils.Query;

import com.xyj.gulimall.coupon.dao.SeckillSessionDao;
import com.xyj.gulimall.coupon.entity.SeckillSessionEntity;
import com.xyj.gulimall.coupon.service.SeckillSessionService;


@Service("seckillSessionService")
public class SeckillSessionServiceImpl extends ServiceImpl<SeckillSessionDao, SeckillSessionEntity> implements SeckillSessionService {


    @Autowired
    SeckillSkuRelationService seckillSkuRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SeckillSessionEntity> page = this.page(
                new Query<SeckillSessionEntity>().getPage(params),
                new QueryWrapper<SeckillSessionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<SeckillSessionEntity> getLatest3DaySession() {
        LocalDate now = LocalDate.now();
        LocalDate plus2Days = now.plusDays(2);
        LocalDateTime startTime = LocalDateTime.of(now, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(plus2Days, LocalTime.MAX);
        startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        endTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        List<SeckillSessionEntity> sessionEntityList = this.list(new QueryWrapper<SeckillSessionEntity>().between("start_time", startTime, endTime));

        if(sessionEntityList != null && sessionEntityList.size() > 0){
            return sessionEntityList.stream().peek(seckillSessionEntity -> {
                Long sessionId = seckillSessionEntity.getId();
                List<SeckillSkuRelationEntity> relationEntities = seckillSkuRelationService.list(new QueryWrapper<SeckillSkuRelationEntity>().eq("promotion_session_id", sessionId));
                seckillSessionEntity.setRelationSkus(relationEntities);
            }).collect(Collectors.toList());
        }
        return null;
    }
}