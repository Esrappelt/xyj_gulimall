package com.xyj.gulimall.product.controller;

import java.util.Arrays;
import java.util.Map;

import com.xyj.gulimall.product.entity.BrandEntity;
import com.xyj.gulimall.product.service.BrandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xyj.common.utils.PageUtils;
import com.xyj.common.utils.R;


/**
 * 品牌
 *
 * @author xyj
 * @email xyj@gmail.com
 * @date 2023-06-15 16:16:56
 */
// 如果要开启校验功能 必须开启注解@Validated 大坑！

@RestController
@RequestMapping("product/brand")
public class BrandController {
    Logger logger = LoggerFactory.getLogger(BrandController.class);
    @Autowired
    private BrandService BrandService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = BrandService.queryPage(params);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{brandId}")
    public R info(@PathVariable("brandId") Long brandId) {
        BrandEntity pmsBrand = BrandService.getById(brandId);

        return R.ok().put("brand", pmsBrand);
    }

    /**
     * 保存
     *
     * @Valid是javax中的校验规则 标注上之后就会校验
     * BindingResult必须紧跟@RequestBody 就能拿到校验结果
     */
    @RequestMapping("/save")
    public R save(@RequestBody @Validated BrandEntity brand, BindingResult result) {
        logger.debug("校验通过:{}", brand);
        BrandService.save(brand);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody BrandEntity brand) {
        BrandService.updateDetail(brand);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] brandIds) {
        BrandService.removeByIds(Arrays.asList(brandIds));

        return R.ok();
    }

}
