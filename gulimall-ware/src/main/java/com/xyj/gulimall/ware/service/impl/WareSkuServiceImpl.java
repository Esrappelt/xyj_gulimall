package com.xyj.gulimall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.rabbitmq.client.Channel;
import com.xyj.common.exception.NoStockException;
import com.xyj.common.to.OrderTo;
import com.xyj.common.to.mq.StockDetailTo;
import com.xyj.common.to.mq.StockLockedTo;
import com.xyj.common.utils.R;
import com.xyj.gulimall.ware.entity.WareOrderTaskDetailEntity;
import com.xyj.gulimall.ware.entity.WareOrderTaskEntity;
import com.xyj.gulimall.ware.openfeign.OrderFeignService;
import com.xyj.gulimall.ware.openfeign.ProductFeignService;
import com.xyj.gulimall.ware.service.WareOrderTaskDetailService;
import com.xyj.gulimall.ware.service.WareOrderTaskService;
import com.xyj.gulimall.ware.vo.OrderItemVo;
import com.xyj.gulimall.ware.vo.OrderVo;
import com.xyj.gulimall.ware.vo.SkuHasStockVo;
import com.xyj.gulimall.ware.vo.WareSkuLockVo;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xyj.common.utils.PageUtils;
import com.xyj.common.utils.Query;

import com.xyj.gulimall.ware.dao.WareSkuDao;
import com.xyj.gulimall.ware.entity.WareSkuEntity;
import com.xyj.gulimall.ware.service.WareSkuService;
import org.springframework.transaction.annotation.Transactional;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                new QueryWrapper<WareSkuEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {

        String skuId = (String) params.get("skuId");
        String wareId = (String) params.get("wareId");

        QueryWrapper<WareSkuEntity> wareSkuEntityQueryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(skuId)) {
            wareSkuEntityQueryWrapper.eq("sku_id", skuId);
        }
        if (StringUtils.isNotEmpty(wareId)) {
            wareSkuEntityQueryWrapper.eq("ware_id", wareId);
        }
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                wareSkuEntityQueryWrapper
        );

        return new PageUtils(page);

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {

        //1、判读如果没有这个库存记录新增
        List<WareSkuEntity> wareSkuEntities = wareSkuDao.selectList(
                new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));

        if (wareSkuEntities == null || wareSkuEntities.size() == 0) {
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStockLocked(0);
            //TODO 远程查询sku的名字，如果失败整个事务无需回滚
            //1、自己catch异常
            try {
                R info = productFeignService.info(skuId);
                Map<String, Object> data = (Map<String, Object>) info.get("skuInfo");
                if (info.getCode() == 0) {
                    wareSkuEntity.setSkuName((String) data.get("skuName"));
                }
            } catch (Exception ignored) {

            }
            //添加库存信息
            wareSkuDao.insert(wareSkuEntity);
        } else {
            //修改库存信息
            wareSkuDao.addStock(skuId, wareId, skuNum);
        }

    }

    @Autowired
    WareSkuDao wareSkuDao;
    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    WareOrderTaskService wareOrderTaskService;

    @Autowired
    WareOrderTaskDetailService wareOrderTaskDetailService;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    OrderFeignService orderFeignService;


    @Override
    public List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds) {
        return skuIds.stream().map(skuId -> {
            SkuHasStockVo skuHasStockVo = new SkuHasStockVo();
            // 查询当前skuIds的库存量
            // select sum(stock-stock_locked) from `wms_ware_sku` where sku_id=1
            Long count = wareSkuDao.getSkusStock(skuId);
            skuHasStockVo.setHasStock(count != null && count > 0);
            skuHasStockVo.setSkuId(skuId);
            return skuHasStockVo;
        }).collect(Collectors.toList());
    }

    /**
     * 为某个订单锁定库存
     *
     * @param vo
     * @return
     */
    volatile Boolean allLock = true;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean orderLockStock(WareSkuLockVo vo) {
        /**
         * 保存库存工作单详情信息
         * 追溯
         */
        /**
         * 保存订单任务
         */
        WareOrderTaskEntity wareOrderTaskEntity = new WareOrderTaskEntity();
        wareOrderTaskEntity.setOrderSn(vo.getOrderSn());
        wareOrderTaskService.save(wareOrderTaskEntity);
        List<OrderItemVo> locks = vo.getLocks();
        List<SkuWareHasStock> skuWareHasStocks = locks.stream().map(item -> {
            SkuWareHasStock skuWareHasStock = new SkuWareHasStock();
            Long skuId = item.getSkuId();
            skuWareHasStock.setSkuId(skuId);
            List<Long> wareIds = wareSkuDao.listWareIdHasSkuStock(skuId);
            skuWareHasStock.setWareId(wareIds);
            skuWareHasStock.setNum(item.getCount());
            return skuWareHasStock;
        }).collect(Collectors.toList());
        for (SkuWareHasStock skuWareHasStock : skuWareHasStocks) {
            Long skuId = skuWareHasStock.getSkuId();
            List<Long> wareIds = skuWareHasStock.getWareId();
            Integer num = skuWareHasStock.getNum();
            if (wareIds == null || wareIds.size() == 0) {
                throw new NoStockException(skuId);
            }
            for (Long wareId : wareIds) {
                Long count = wareSkuDao.lockSkuStock(skuId, wareId, num);
                if (count == 1) {
                    allLock = true;
                    WareOrderTaskDetailEntity wareOrderTaskDetailEntity = new WareOrderTaskDetailEntity();
                    wareOrderTaskDetailEntity.setSkuId(skuId);
                    wareOrderTaskDetailEntity.setSkuName("");
                    wareOrderTaskDetailEntity.setWareId(wareId);
                    wareOrderTaskDetailEntity.setTaskId(wareOrderTaskEntity.getId());
                    wareOrderTaskDetailEntity.setSkuNum(skuWareHasStock.getNum());
                    wareOrderTaskDetailEntity.setLockStatus(1L);
                    wareOrderTaskDetailService.save(wareOrderTaskDetailEntity);
                    // 锁定后，放入MQ
                    // 一次发一个消息
                    StockLockedTo stockLockedTo = new StockLockedTo();
                    stockLockedTo.setId(wareOrderTaskEntity.getId());
                    StockDetailTo stockDetailTo = new StockDetailTo();
                    BeanUtils.copyProperties(wareOrderTaskDetailEntity, stockDetailTo);
                    stockLockedTo.setDetail(stockDetailTo);
                    rabbitTemplate.convertAndSend("stock-event-exchange", "stock.locked", stockLockedTo);
                    break;
                }
            }
            if (!allLock) {
                throw new NoStockException(skuId);
            }
        }
        // 锁定成功
        return true;
    }

    @Override
    public void doUnLockStock(StockLockedTo stockLockedTo) {
        //  解锁
        StockDetailTo detail = stockLockedTo.getDetail();
        Long detailId = detail.getId();
        // 查询数据库 关于该订单的锁库存信息
        WareOrderTaskDetailEntity detailEntity = wareOrderTaskDetailService.getById(detailId);
        if (detailEntity != null) {
            // 有: 回滚库存
            // 1.没有这个订单。 必须解锁(订单失败情况)
            // 2.有这个订单 。(订单存在的情况)
            //            1. 订单状态：已取消：解锁库存
            //                       未取消：不需解锁
            Long id = stockLockedTo.getId();
            WareOrderTaskEntity taskEntity = wareOrderTaskService.getById(id);
            if (taskEntity != null) {
                String orderSn = taskEntity.getOrderSn();
                R r = orderFeignService.getOrderSn(orderSn);
                if (r.getCode() == 0) {
                    // 订单返回成功
                    OrderVo orderVo = r.getData(new TypeReference<OrderVo>() {
                    });
                    if (orderVo == null || orderVo.getStatus() == 4) {
                        // 订单都不存在或者订单被取消了，解锁库存
                        System.out.println("订单被取消了");
                        if (detailEntity.getLockStatus() == 1) {
                            // 当前库存工作单详情，状态为1，即未解锁才可以解锁
                            System.out.println("解锁");
                            unLockStock(detailEntity);
                        }
//                        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                    }else{
                        switch (orderVo.getStatus()) {
                            case 1:
                                System.out.println("订单已付款，无需解锁库存!");
                                break;
                            case 2:
                                System.out.println("订单已发货，无需解锁库存!");
                                break;
                            case 3:
                                System.out.println("订单已完成，无需解锁库存!");
                                break;
                            case 5:
                                System.out.println("订单售后中，无需解锁库存!");
                                break;
                            case 6:
                                System.out.println("订单售后完成，无需解锁库存!");
                                break;
                        }
                    }
                } else {
                    throw new RuntimeException("Order远程服务调用失败!");
                }
            }
        } else {
            // 没有:库存锁定失败，库存都回滚了 -----无需解锁
            // 消息拒绝后 重新放到队列里面
//            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }

    @Transactional
    @Override
    public void doUnLockStock(OrderTo order) {
        // 防止订单服务卡顿，导致订单状态消息一直改不了， 库存消息优先到期，结果什么都没做就走了  结果解锁不了库存
        // 查询库存最新状态，防止重复解锁库存
        WareOrderTaskEntity wareOrderTaskEntity = wareOrderTaskService.getOrderTaskByOrderSn(order.getOrderSn());
        Long id = wareOrderTaskEntity.getId();
        List<WareOrderTaskDetailEntity> taskDetailEntities = wareOrderTaskDetailService.list(
                new QueryWrapper<WareOrderTaskDetailEntity>()
                        .eq("task_id", id)
                        .eq("lock_status", 1));
        for (WareOrderTaskDetailEntity taskDetailEntity : taskDetailEntities) {
            unLockStock(taskDetailEntity);
        }
    }

    private void unLockStock(WareOrderTaskDetailEntity detailEntity) {
        Long skuId = detailEntity.getSkuId();
        Long wareId = detailEntity.getWareId();
        Long detailId = detailEntity.getId();
        Integer skuNum = detailEntity.getSkuNum();
        wareSkuDao.unLockStock(skuId, wareId, skuNum, detailId);
        WareOrderTaskDetailEntity wareOrderTaskDetailEntity = new WareOrderTaskDetailEntity();
        wareOrderTaskDetailEntity.setId(detailEntity.getId());
        wareOrderTaskDetailEntity.setLockStatus(2L);//变为已解锁
        wareOrderTaskDetailService.updateById(wareOrderTaskDetailEntity);
    }
}

@Data
class SkuWareHasStock {
    private Long skuId;
    private Integer num;
    private List<Long> wareId;
}