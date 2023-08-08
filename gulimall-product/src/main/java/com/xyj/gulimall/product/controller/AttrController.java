package com.xyj.gulimall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.xyj.gulimall.product.entity.ProductAttrValueEntity;
import com.xyj.gulimall.product.service.ProductAttrValueService;
import com.xyj.gulimall.product.vo.AttrGroupRelationVo;
import com.xyj.gulimall.product.vo.AttrRespVo;
import com.xyj.gulimall.product.vo.AttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.xyj.gulimall.product.entity.AttrEntity;
import com.xyj.gulimall.product.service.AttrService;
import com.xyj.common.utils.PageUtils;
import com.xyj.common.utils.R;



/**
 * 商品属性
 *
 * @author xyj
 * @email xyj@gmail.com
 * @date 2023-06-19 20:55:29
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;

    @Autowired
    ProductAttrValueService productAttrValueService;




    /**
     * /product/attr/base/listforspu/{spuId}
     */

    @RequestMapping("/base/listforspu/{spuId}")
    public R baseListForSpu(@PathVariable("spuId") Long spuId){
        List<ProductAttrValueEntity> data = productAttrValueService.getBaseListForSpu(spuId);
        return R.ok().put("data", data);
    }
    /**
     * 列表
     */
    @RequestMapping("/{attrType}/list/{catelogId}")
    public R baseList(@RequestParam Map<String, Object> params,
                      @PathVariable("catelogId") Long catelogId,
                      @PathVariable("attrType") String attrType){
        PageUtils page = attrService.queryBaseAttrList(params, catelogId, attrType);
        return R.ok().put("page", page);
    }
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
    public R info(@PathVariable("attrId") Long attrId){
        AttrRespVo attrRespVo = attrService.queryAttrInfo(attrId);
        System.out.println(attrRespVo);
        return R.ok().put("attr", attrRespVo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrVo attrVo){
        attrService.saveAttr(attrVo);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrVo attrVo){
        attrService.updateAttr(attrVo);
        return R.ok();
    }
    /**
     * 修改
     */
    @PostMapping("/update/{spuId}")
    public R updateAttr(@PathVariable Long spuId,
                        @RequestBody List<ProductAttrValueEntity> entities){
        productAttrValueService.updateAttr(spuId,entities);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrIds){
		attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }

}
