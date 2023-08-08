package com.xyj.gulimall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.xyj.gulimall.product.entity.AttrEntity;
import com.xyj.gulimall.product.entity.AttrGroupEntity;
import com.xyj.gulimall.product.service.AttrAttrgroupRelationService;
import com.xyj.gulimall.product.service.AttrGroupService;
import com.xyj.gulimall.product.service.AttrService;
import com.xyj.gulimall.product.service.CategoryService;
import com.xyj.gulimall.product.vo.AttrGroupRelationVo;
import com.xyj.gulimall.product.vo.AttrGroupWithAttrsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.xyj.common.utils.PageUtils;
import com.xyj.common.utils.R;



/**
 * 属性分组
 *
 * @author xyj
 * @email xyj@gmail.com
 * @date 2023-06-15 16:16:56
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private AttrAttrgroupRelationService relationService;
    @RequestMapping("/{attrGroupId}/attr/relation")
    public R attrRelation(@PathVariable("attrGroupId") Long attrGroupId){
        List<AttrEntity> entityList = attrService.getAttrRelation(attrGroupId);
        return R.ok().put("data", entityList);
    }

    /**
     * /product/attrgroup/attr/relation
     */
    @PostMapping("/attr/relation")
    public R addRelation(@RequestBody List<AttrGroupRelationVo> attrGroupRelationVos){
        relationService.saveBatch(attrGroupRelationVos);
        return R.ok();
    }
    /**
     * 列表
     */
    @RequestMapping("/list/{catelogId}")
    public R list(@RequestParam Map<String, Object> params,
                  @PathVariable(value = "catelogId") Long catelogId){
        PageUtils page = attrGroupService.queryPage(params, catelogId);
        return R.ok().put("page", page);
    }
    /**
     * /product/attrgroup/{catelogId}/withattr
     */
    @GetMapping("/{catelogId}/withattr")
    public R getAttrGroupWithAttrs(@PathVariable("catelogId") Long catelogId){
        List<AttrGroupWithAttrsVo> attrGroupWithAttrsByCateLogId = attrGroupService.getAttrGroupWithAttrsByCateLogId(catelogId);
        return R.ok().put("data", attrGroupWithAttrsByCateLogId);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){
        AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);
        Long catelogId = attrGroup.getCatelogId();
        Long[] paths = categoryService.fileCateLogPath(catelogId);
        attrGroup.setCatelogPath(paths);
        Arrays.stream(paths).forEach(System.out::println);
        System.out.println(attrGroup);
        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
        attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
        attrGroupService.updateById(attrGroup);
        return R.ok();
    }
    /**
     * 删除 并支持批量删除  所以使用数组的方式
     */
    @PostMapping("/attr/relation/delete")
    public R deleteRelation(@RequestBody AttrGroupRelationVo[] attrGroupRelationVos){
        attrService.deleteRelation(attrGroupRelationVos);
        return R.ok();
    }
    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrGroupIds){
        attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }
    // /product/attrgroup/{attrgroupId}/noattr/relation
    @GetMapping("/{attrgroupId}/noattr/relation")
    public R attrNoRelation(@PathVariable("attrgroupId") Long attrgroupId,
                            @RequestParam Map<String, Object> params){
        PageUtils pageUtils = attrService.getNoRelationAttr(attrgroupId, params);
        return R.ok().put("page", pageUtils);
    }


}
