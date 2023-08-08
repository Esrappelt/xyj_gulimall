package com.xyj.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xyj.common.utils.PageUtils;
import com.xyj.gulimall.order.entity.OrderReturnReasonEntity;

import java.util.Map;

/**
 * 退货原因
 *
 * @author xyj
 * @email xyj@gmail.com
 * @date 2023-06-15 18:32:36
 */
public interface OrderReturnReasonService extends IService<OrderReturnReasonEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

