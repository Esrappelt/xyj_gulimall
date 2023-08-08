package com.xyj.gulimall.product.web;

import com.xyj.gulimall.product.entity.CategoryEntity;
import com.xyj.gulimall.product.service.CategoryService;
import com.xyj.gulimall.product.vo.Catelog2Vo;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Author jie}
 * @Date 2023/6/26 3:30}
 */
@Controller
public class IndexController {
    @Autowired
    CategoryService categoryService;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @GetMapping({"/", "/index.html"})
    public String indexPage(Model model) {
        // 1.查出所有的一级分类
        List<CategoryEntity> categoryEntityList = categoryService.getLevelOneCategories();
        model.addAttribute("categories", categoryEntityList);
        return "index";
    }

    @RequestMapping("/hello")
    @ResponseBody
    public String hello() {
        // 这个就和j.u.c下的lock一样的用法
        RLock lock = redissonClient.getLock("my-lock");
        try {
            lock.lock(10, TimeUnit.SECONDS);// 阻塞式等待,超时时间是10s 自动解锁
            Thread.sleep(30000);
            System.out.println("执行业务!");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            System.out.println("释放锁" + Thread.currentThread().getName());
            lock.unlock();
        }
        return "hello";
    }

    @RequestMapping("/index/catalog.json")
    @ResponseBody
    public Map<String, List<Catelog2Vo>> getCatalogJson() {
        return categoryService.getCatalogJson();
    }




    @GetMapping("/write")
    @ResponseBody
    public void writeValue() {
        RReadWriteLock lock = redissonClient.getReadWriteLock("rw-lock");// 名字一样 就是同一把锁
        RLock wlock = lock.writeLock();
        try {
            wlock.lock();
            stringRedisTemplate.opsForValue().set("value", UUID.randomUUID().toString());
        }catch (Exception ignored){

        }finally {
            wlock.unlock();
        }
    }



    @GetMapping("/read")
    @ResponseBody
    public String readValue() {
        RReadWriteLock lock = redissonClient.getReadWriteLock("rw-lock");
        RLock rLock = lock.readLock(); // 读锁
        String s = "";
        try {
            rLock.lock();
            s = stringRedisTemplate.opsForValue().get("value");
        }catch (Exception ignored){

        }finally {
            rLock.unlock();
        }
        return s;

    }


}
