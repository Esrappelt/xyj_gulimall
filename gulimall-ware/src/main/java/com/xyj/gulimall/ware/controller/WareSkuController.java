package com.xyj.gulimall.ware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.xyj.common.exception.BizCideEnume;
import com.xyj.common.to.SkuHasStockTo;
import com.xyj.common.utils.MyBeanUtils;
import com.xyj.common.exception.NoStockException;
import com.xyj.gulimall.ware.vo.SkuHasStockVo;
import com.xyj.gulimall.ware.vo.WareSkuLockVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.xyj.gulimall.ware.entity.WareSkuEntity;
import com.xyj.gulimall.ware.service.WareSkuService;
import com.xyj.common.utils.PageUtils;
import com.xyj.common.utils.R;



/**
 * 商品库存
 *
 * @author xyj
 * @email xyj@gmail.com
 * @date 2023-06-15 18:36:07
 */
@RestController
@RequestMapping("ware/waresku")
public class WareSkuController {
    @Autowired
    private WareSkuService wareSkuService;


    /**
     * 订单锁定库存操作
     */
    @PostMapping("/lock/order")
    public R orderLockStock(@RequestBody WareSkuLockVo wareSkuLockVo){
        try {
            Boolean results = wareSkuService.orderLockStock(wareSkuLockVo);
            return R.ok().setData(results);
        }catch (NoStockException e){
            return R.error(BizCideEnume.NO_STOCK_EXCEPTION.getCode(), BizCideEnume.NO_STOCK_EXCEPTION.getMsg());
        }
    }


    /**
     * 查询是否有库存
     */
    @PostMapping("/hasStock")
    public R getSkuHasStock(@RequestBody List<Long> skuIds){
        // 就返回skuId和hasStock
        List<SkuHasStockVo> skuHasStockVos = wareSkuService.getSkuHasStock(skuIds);
        // 构造提供者
        Supplier<SkuHasStockTo> skuHasStockToSupplier = SkuHasStockTo::new;
        List<SkuHasStockTo> skuHasStockTos = MyBeanUtils.copyListProperties(skuHasStockVos, skuHasStockToSupplier);
        return R.ok().put("data", skuHasStockTos);
    }


    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wareSkuService.queryPageByCondition(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		WareSkuEntity wareSku = wareSkuService.getById(id);

        return R.ok().put("wareSku", wareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WareSkuEntity wareSku){
		wareSkuService.save(wareSku);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WareSkuEntity wareSku){
		wareSkuService.updateById(wareSku);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		wareSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
