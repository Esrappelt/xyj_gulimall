package com.xyj.gulimall.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.xyj.common.exception.NoStockException;
import com.xyj.common.to.OrderTo;
import com.xyj.common.utils.R;
import com.xyj.common.vo.MemberResponseVo;
import com.xyj.gulimall.order.constant.OrderConstant;
import com.xyj.gulimall.order.entity.OrderItemEntity;
import com.xyj.gulimall.order.entity.PaymentInfoEntity;
import com.xyj.gulimall.order.feign.CartFeignService;
import com.xyj.gulimall.order.feign.MemberFeignService;
import com.xyj.gulimall.order.feign.ProductFeignService;
import com.xyj.gulimall.order.feign.WmsFeignService;
import com.xyj.gulimall.order.service.OrderItemService;
import com.xyj.gulimall.order.service.PaymentInfoService;
import com.xyj.common.to.SeckillOrderTo;
import com.xyj.gulimall.order.vo.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xyj.common.utils.PageUtils;
import com.xyj.common.utils.Query;

import com.xyj.gulimall.order.dao.OrderDao;
import com.xyj.gulimall.order.entity.OrderEntity;
import com.xyj.gulimall.order.service.OrderService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import static com.xyj.gulimall.order.interceptor.LoginUserInterceptor.loginUser;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    @Autowired
    MemberFeignService memberFeignService;


    @Autowired
    ThreadPoolExecutor executor;

    @Autowired
    CartFeignService cartFeignService;


    @Autowired
    WmsFeignService wmsFeignService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    OrderDao orderDao;

    @Autowired
    OrderItemService orderItemService;

    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    PaymentInfoService paymentInfoService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException {
        OrderConfirmVo orderConfirmVo = new OrderConfirmVo();
        MemberResponseVo memberResponseVo = loginUser.get();
//        System.out.println("主线程："+Thread.currentThread().getId());
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        // 1. 收货的地址列表
        CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
//            System.out.println("memberFeignService线程："+Thread.currentThread().getId());
            // 由于是不同线程 所以需要进行设置 TheadLocal的东西不能共享
            RequestContextHolder.setRequestAttributes(requestAttributes);
            Long memberId = memberResponseVo.getId();
            List<MemberAddressVo> memberAddressVos = memberFeignService.getAddress(memberId);
            orderConfirmVo.setAddress(memberAddressVos);
        }, executor);
        // 2. 远程商品的购物项
        CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> {
//            System.out.println("cartFeignService线程："+Thread.currentThread().getId());
            RequestContextHolder.setRequestAttributes(requestAttributes);
            List<OrderItemVo> userCartItems = cartFeignService.getCurrentUserCartItems();
            orderConfirmVo.setItems(userCartItems);
        }, executor).thenRunAsync(() -> {
            List<OrderItemVo> items = orderConfirmVo.getItems();
            List<Long> skuIdList = items.stream().map(OrderItemVo::getSkuId).collect(Collectors.toList());
            R skuHasStock = wmsFeignService.getSkuHasStock(skuIdList);
            if (skuHasStock.getCode() == 0) {
                List<SkuStockVo> hasStockData = skuHasStock.getData("data", new TypeReference<List<SkuStockVo>>() {
                });
                if (hasStockData != null) {
                    orderConfirmVo.setStocks(hasStockData.stream().collect(Collectors.toMap(SkuStockVo::getSkuId, SkuStockVo::getHasStock)));
                }
            }
        }, executor);
        // 3.查询用户积分
        Integer integration = memberResponseVo.getIntegration();
        orderConfirmVo.setIntegration(integration);

        // 4. 订单总额，应付总额
        // 自动计算

        // 防重令牌
        String token = UUID.randomUUID().toString().replace("-", "");
        stringRedisTemplate.opsForValue().set(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberResponseVo.getId(), token, 30, TimeUnit.MINUTES);
        orderConfirmVo.setOrderToken(token);
        CompletableFuture.allOf(future1, future2).get();
        return orderConfirmVo;
    }

    /**
     * 创建订单，验证令牌，锁库存。。等等
     * 下单成功 去支付成功页
     * 失败 重新确认订单信息
     * <p>
     * 下订单成功，订单过期了没有支付，要进行
     * 订单成功，库存锁定成功，调用失败就要回滚
     *
     * @param orderSubmitVo
     * @return
     */

    @Transactional(rollbackFor = Exception.class) // 本地事务 无法解决远程事务一致性问题
//    @GlobalTransactional(rollbackFor = Exception.class)  // 所以必须使用分布式事务
    @Override
    public SubmitOrderResponseVo submitOrder(OrderSubmitVo orderSubmitVo) {
        SubmitOrderResponseVo responseVo = new SubmitOrderResponseVo();
        MemberResponseVo memberResponseVo = loginUser.get();
        // 验证令牌
        String orderToken = orderSubmitVo.getOrderToken();
        Long userId = memberResponseVo.getId();
        String key = OrderConstant.USER_ORDER_TOKEN_PREFIX + userId;
        String redisLuaScript = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        // 验证+删除 原子操作
        Long result = stringRedisTemplate.execute(new DefaultRedisScript<Long>(redisLuaScript, Long.class), Collections.singletonList(key), orderToken);
        if (result != null) {
            if (result == 0L) {
                // 验证失败
                responseVo.setCode(1);
            } else {
                // 验证成功
                OrderCreateTo order = createOrder(orderSubmitVo);
                BigDecimal payAmount = order.getOrder().getPayAmount();
                BigDecimal payPrice = orderSubmitVo.getPayPrice();
                if (Math.abs(payAmount.subtract(payPrice).doubleValue()) < 0.01) {
                    // 订单保存
                    saveOder(order);
                    // 库存锁定
                    WareSkuLockVo wareSkuLockVo = new WareSkuLockVo();
                    wareSkuLockVo.setOrderSn(order.getOrder().getOrderSn());
                    List<OrderItemVo> orderItemVos = order.getOrderItems().stream().map(orderItemEntity -> {
                        OrderItemVo orderItemVo = new OrderItemVo();
                        orderItemVo.setSkuId(orderItemEntity.getSkuId());
                        orderItemVo.setCount(orderItemEntity.getSkuQuantity());
                        orderItemVo.setTitle(orderItemEntity.getSkuName());
                        return orderItemVo;
                    }).collect(Collectors.toList());
                    wareSkuLockVo.setLocks(orderItemVos);
                    R r = wmsFeignService.orderLockStock(wareSkuLockVo);
                    if (r.getCode() == 0) {
                        // 锁定成功
                        responseVo.setCode(0);
                        responseVo.setOrder(order.getOrder());
                        //  TODO 远程扣减积分
                        // 订单创建成功，发送订单消息给MQ
                        rabbitTemplate.convertAndSend("order-event-exchange", "order.create.order", order.getOrder());
                    } else {
                        // 锁定失败
                        responseVo.setCode(3);
                        throw new NoStockException();
                    }
                } else {
                    responseVo.setCode(2);
                }
            }
        }
        return responseVo;
    }

    @Override
    public OrderEntity getOrderSn(String orderSn) {
        return this.baseMapper.getOrderSn(orderSn);
    }

    @Override
    public void closeOrder(OrderEntity orderEntity) {
        // 查询当前订单的最新状态
        OrderEntity order = this.getById(orderEntity.getId());
        // 关单
        if (OrderConstant.OrderStatus.CREATE_NEW.getCode().equals(order.getStatus())) {
            System.out.println("收到过期订单，准备关闭订单：" + orderEntity.getOrderSn());
            OrderEntity orderEntity1 = new OrderEntity();
            orderEntity1.setId(orderEntity.getId());
            orderEntity1.setStatus(OrderConstant.OrderStatus.CANCLED.getCode());
            this.updateById(orderEntity1);
            // 发给MQ 进行释放库存
            OrderTo orderTo = new OrderTo();
            BeanUtils.copyProperties(order, orderTo);
            // 保证消息一定要发出去，手动ack机制
            try {
                // TODO 每一个发送的消息 都要有日志记录
                rabbitTemplate.convertAndSend("order-event-exchange", "order.release.other", orderTo);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else {
            switch (orderEntity.getStatus()) {
                case 1:
                    System.out.println("订单已付款，无需解锁库存!");
                    break;
                case 2:
                    System.out.println("订单已发货，无需解锁库存!");
                    break;
                case 3:
                    System.out.println("订单已完成，无需解锁库存!");
                    break;
                case 4:
                    System.out.println("订单已取消，无需解锁库存!");
                    break;
                case 5:
                    System.out.println("订单售后中，无需解锁库存!");
                    break;
                case 6:
                    System.out.println("订单售后完成，无需解锁库存!");
                    break;
            }
        }
    }

    @Override
    public PayVo getOrderPay(String orderSn) {
        List<OrderItemEntity> list = orderItemService.list(
                new QueryWrapper<OrderItemEntity>()
                        .eq("order_sn", orderSn)
        );
        OrderItemEntity orderItemEntity = list.get(0);
        PayVo payVo = new PayVo();
        OrderEntity order = this.getOrderSn(orderSn);
        payVo.setTotal_amount(order.getPayAmount().setScale(2, RoundingMode.UP).toString());
        payVo.setOut_trade_no(UUID.randomUUID().toString());
        payVo.setSubject(orderItemEntity.getSkuName());
        payVo.setBody(orderItemEntity.getSkuAttrsVals());
        return payVo;
    }

    @Override
    public PageUtils queryPageWithItem(Map<String, Object> params) {
        MemberResponseVo memberResponseVo = loginUser.get();
        if (memberResponseVo == null) {
            System.out.println("没有登录");
            IPage<OrderEntity> page = this.page(new Query<OrderEntity>().getPage(params),
                    new QueryWrapper<OrderEntity>()
            );
            return new PageUtils(page);
        }
        IPage<OrderEntity> page = this.page(new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>().eq("member_id", memberResponseVo.getId()).orderByDesc("id")
        );
        IPage<OrderPageVo> convertPage = page.convert(orderEntity -> {
            OrderPageVo orderPageVo = new OrderPageVo();
            BeanUtils.copyProperties(orderEntity, orderPageVo);
            List<OrderItemEntity> itemEntities = orderItemService.list(new QueryWrapper<OrderItemEntity>().eq("order_sn", orderEntity.getOrderSn()));
            orderPageVo.setItemEntities(itemEntities);
            return orderPageVo;
        });
        return new PageUtils(convertPage);
    }

    @Transactional
    @Override
    public Boolean pay(PayVo payVo, String orderSn) {
        try {
            long start = System.currentTimeMillis();
            PaymentInfoEntity paymentInfoEntity = new PaymentInfoEntity();
            paymentInfoEntity.setOrderSn(orderSn);
            paymentInfoEntity.setAlipayTradeNo(payVo.getOut_trade_no());
            paymentInfoEntity.setPaymentStatus("TRADE_SUCCESS"); // 支付成功
            paymentInfoEntity.setCallbackTime(new Date());
            paymentInfoEntity.setSubject(payVo.getBody());
            paymentInfoEntity.setTotalAmount(new BigDecimal(payVo.getTotal_amount()));
            // TODO 验证签名
            // 修改订单状态
            paymentInfoService.save(paymentInfoEntity);
            this.baseMapper.updateOrderStatus(orderSn, OrderConstant.OrderStatus.PAYED.getCode());
            System.out.println("状态修改成功");
            long end = System.currentTimeMillis();
            if (end - start >= 60) {
                // 回滚数据
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    @Transactional
    @Override
    public void createSeckillOrder(SeckillOrderTo seckillOrder) {
        // 保存订单信息
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderSn(seckillOrder.getOrderSn());
        orderEntity.setMemberId(seckillOrder.getMemberId());
        orderEntity.setStatus(OrderConstant.OrderStatus.CREATE_NEW.getCode());
        BigDecimal payAmount = seckillOrder.getSeckillPrice().multiply(new BigDecimal(seckillOrder.getNum()));
        orderEntity.setPayAmount(payAmount);
        this.save(orderEntity);
        // TODO 保存订单项的问题
        OrderItemEntity orderItemEntity = new OrderItemEntity();
        orderItemEntity.setOrderSn(seckillOrder.getOrderSn());
        orderItemEntity.setRealAmount(payAmount);
        orderItemEntity.setSkuQuantity(seckillOrder.getNum());
        // TODO 还有其他一些详细信息......
        orderItemService.save(orderItemEntity);
    }

    /**
     * 保存订单
     *
     * @param order
     */
    private void saveOder(OrderCreateTo order) {
        OrderEntity orderEntity = order.getOrder();
        orderEntity.setModifyTime(new Date());
        this.save(orderEntity);
        List<OrderItemEntity> orderItems = order.getOrderItems();
        orderItemService.saveBatch(orderItems);
    }

    private OrderCreateTo createOrder(OrderSubmitVo orderSubmitVo) {
        OrderCreateTo orderCreateTo = new OrderCreateTo();
        //创建订单

        // 1.生成订单号
        OrderEntity orderEntity = buildOrder(orderSubmitVo);
        // 2.获取所有的订单项 即购物车的商品
        List<OrderItemEntity> userCartItems = getCurrentUserCartItems(orderSubmitVo, orderEntity.getOrderSn());
        // 3.验证价格
        if (userCartItems != null) computePrice(orderEntity, userCartItems);
        orderCreateTo.setOrder(orderEntity);
        orderCreateTo.setOrderItems(userCartItems);
        return orderCreateTo;
    }

    private void computePrice(OrderEntity orderEntity, List<OrderItemEntity> userCartItems) {
        // 订单价格相关
        BigDecimal total = new BigDecimal("0.0");
        BigDecimal counpon = new BigDecimal("0.0");
        BigDecimal promotion = new BigDecimal("0.0");
        BigDecimal intergration = new BigDecimal("0.0");
        BigDecimal giftIntegration = new BigDecimal("0.0");
        BigDecimal giftGrowth = new BigDecimal("0.0");
        for (OrderItemEntity entity : userCartItems) {
            BigDecimal realAmount = entity.getRealAmount();
            total = total.add(realAmount);
            counpon = counpon.add(entity.getCouponAmount());
            promotion = promotion.add(entity.getPromotionAmount());
            intergration = intergration.add(entity.getIntegrationAmount());
            giftIntegration = giftIntegration.add(new BigDecimal(entity.getGiftIntegration()));
            giftGrowth = giftGrowth.add(new BigDecimal(entity.getGiftGrowth()));
        }
        orderEntity.setTotalAmount(total);
        orderEntity.setPayAmount(total.add(orderEntity.getFreightAmount()));
        orderEntity.setPromotionAmount(promotion);
        orderEntity.setIntegration(intergration.intValue());
        orderEntity.setCouponAmount(counpon);
        // 订单状态
        orderEntity.setStatus(OrderConstant.OrderStatus.CREATE_NEW.getCode());
        orderEntity.setConfirmStatus(1);
        //设置积分信息
        orderEntity.setIntegration(giftIntegration.intValue());
        orderEntity.setGrowth(giftGrowth.intValue());
        orderEntity.setDeleteStatus(0);// 未删除
    }

    private List<OrderItemEntity> getCurrentUserCartItems(OrderSubmitVo orderSubmitVo, String orderSn) {
        List<OrderItemVo> userCartItems = cartFeignService.getCurrentUserCartItems();
        if (userCartItems != null && userCartItems.size() > 0) {
            return userCartItems.stream().map(cartItem -> {
                OrderItemEntity orderItemEntity = buildOrderItem(cartItem);
                orderItemEntity.setOrderSn(orderSn);
                return orderItemEntity;
            }).collect(Collectors.toList());
        }
        return null;
    }

    private OrderEntity buildOrder(OrderSubmitVo orderSubmitVo) {
        String orderSn = IdWorker.getTimeId();
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderSn(orderSn);
        orderEntity.setMemberId(loginUser.get().getId());
        orderEntity.setMemberUsername(loginUser.get().getUsername());
        // 收货地址信息
        R fareRes = wmsFeignService.getFare(orderSubmitVo.getAddrId());
        if (fareRes.getCode() == 0) {
            FareVo fareVo = fareRes.getData(new TypeReference<FareVo>() {
            });
            if (fareVo != null) {
                orderEntity.setReceiverDetailAddress(fareVo.getAddress().getDetailAddress());
                orderEntity.setReceiverCity(fareVo.getAddress().getCity());
                orderEntity.setFreightAmount(fareVo.getFare());
                orderEntity.setReceiverName(fareVo.getAddress().getName());
                orderEntity.setReceiverPhone(fareVo.getAddress().getPhone());
                orderEntity.setReceiverPostCode(fareVo.getAddress().getPostCode());
                orderEntity.setReceiverProvince(fareVo.getAddress().getProvince());
                orderEntity.setReceiverRegion(fareVo.getAddress().getRegion());

            }
        }
        return orderEntity;
    }

    private OrderItemEntity buildOrderItem(OrderItemVo cartItem) {
        OrderItemEntity orderItemEntity = new OrderItemEntity();
        // 订单信息

        // 商品spu信息
        Long skuId = cartItem.getSkuId();
        R spuInfo = productFeignService.getSpuInfoBySkuId(skuId);
        if (spuInfo.getCode() == 0) {
            SpuInfoVo spuInfoData = spuInfo.getData(new TypeReference<SpuInfoVo>() {
            });
            if (spuInfoData != null) {
                orderItemEntity.setSpuId(spuInfoData.getId());
                orderItemEntity.setSpuBrand(spuInfoData.getBrandId().toString());
                orderItemEntity.setSpuName(spuInfoData.getSpuName());
                orderItemEntity.setCategoryId(spuInfoData.getCatalogId());
            }
        }
        // 商品sku信息
        orderItemEntity.setSkuId(cartItem.getSkuId());
        orderItemEntity.setSkuName(cartItem.getTitle());
        orderItemEntity.setSkuPic(cartItem.getImage());
        orderItemEntity.setSkuPrice(cartItem.getPrice());
        orderItemEntity.setSkuAttrsVals(StringUtils.collectionToDelimitedString(cartItem.getSkuAttr(), ";"));
        orderItemEntity.setSkuQuantity(cartItem.getCount());
        // 优惠信息
        // 积分信息
        orderItemEntity.setGiftGrowth(cartItem.getPrice().multiply(new BigDecimal(cartItem.getCount())).intValue());
        orderItemEntity.setGiftIntegration(cartItem.getPrice().multiply(new BigDecimal(cartItem.getCount())).intValue());
        orderItemEntity.setIntegrationAmount(new BigDecimal("0"));
        orderItemEntity.setPromotionAmount(new BigDecimal("0"));
        orderItemEntity.setCouponAmount(new BigDecimal("0"));
        BigDecimal readlAmount = orderItemEntity.getSkuPrice().multiply(new BigDecimal(orderItemEntity.getSkuQuantity()));
        readlAmount = readlAmount
                .subtract(orderItemEntity.getCouponAmount())
                .subtract(orderItemEntity.getPromotionAmount())
                .subtract(orderItemEntity.getIntegrationAmount());
        orderItemEntity.setRealAmount(readlAmount);
        return orderItemEntity;
    }
}