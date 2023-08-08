package com.xyj.gulimall.product;


import com.xyj.gulimall.product.dao.SkuSaleAttrValueDao;
import com.xyj.gulimall.product.service.AttrGroupService;
import com.xyj.gulimall.product.service.BrandService;
import com.xyj.gulimall.product.service.CategoryService;
import com.xyj.gulimall.product.vo.SkuItemSaleAttrVo;
import com.xyj.gulimall.product.vo.SpuItemAttrGroupVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
class ProductApplicationTests {
    @Autowired
    BrandService brandService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    AttrGroupService attrGroupService;

    @Autowired
    SkuSaleAttrValueDao skuSaleAttrValueDao;


    @Test
    void test3(){
        List<SkuItemSaleAttrVo> saleAttrsBySpuId = skuSaleAttrValueDao.getSaleAttrsBySpuId(7L);
        System.out.println(saleAttrsBySpuId);
    }

    @Test
    void test2(){
        List<SpuItemAttrGroupVo> attrGroupWithAttrsBySpuId = attrGroupService.getAttrGroupWithAttrsBySpuId(78L, 225L);
        System.out.println(attrGroupWithAttrsBySpuId);
    }


    @Test
    void contextLoads() {
        testUpload();
    }
    void testUpload()  {
        Long[] path = categoryService.fileCateLogPath(225L);
        List<Long> list = Arrays.asList(path);
        log.debug("path:{}", list);
    }

    @Test
    void redistTest(){
        //定义数据类型
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        // 保存操作
        ops.set("hello", "world"+ UUID.randomUUID().toString());
        // 获取操作
        String hello = ops.get("hello");
        System.out.println(hello);
    }

    @Test
    void redissonTest(){
        System.out.println(redissonClient);
    }














}
