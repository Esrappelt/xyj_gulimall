package com.xyj.gulimall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.xyj.gulimall.product.service.CategoryBrandRelationService;
import com.xyj.gulimall.product.vo.Catelog2Vo;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xyj.common.utils.PageUtils;
import com.xyj.common.utils.Query;

import com.xyj.gulimall.product.dao.CategoryDao;
import com.xyj.gulimall.product.entity.CategoryEntity;
import com.xyj.gulimall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    private CategoryDao pmsCategoryDao;
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RedissonClient redissonClient;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        // 1.查出所有的分类
        List<CategoryEntity> pmsCategoryEntities = baseMapper.selectList(null);
        // 2.组装成父子的树形结构
        return pmsCategoryEntities.stream().filter((categoryEntity) -> {
            return categoryEntity.getParentCid() == 0; // get方法  getParentCid() lombok简化了
        }).peek((menu) -> {
            // 获取当前菜单menu的子菜单 childrens
            menu.setChildren(getChildrens(menu, pmsCategoryEntities));
        }).sorted(Comparator.comparing(CategoryEntity::getSort)).collect(Collectors.toList());
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
        // TODO 检查当前删除的菜单，是否被别的地方引用

        baseMapper.deleteBatchIds(asList);
    }

    @Override
    public Long[] fileCateLogPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        getcateLogPaths(paths, catelogId);
        Collections.reverse(paths);
        return paths.toArray(new Long[0]);
    }

    /**
     * 级联分类需要开启事务
     *
     * @param category
     */
    @Transactional
    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        // 级联更新
        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
    }

    /**
     * 缓存名字为category,key为methodName==>getLevelOneCategories
     * 将数据保存为json格式
     * @return
     */
    @Cacheable(value = {"category"}, key="#root.methodName") // 代表方法的结果需要缓存  缓存有 就不调用方法了 ，缓存没有就调用方法 再进行缓存
    @Override
    public List<CategoryEntity> getLevelOneCategories() {
        return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
    }
    private static final ReentrantLock lock = new ReentrantLock();
    private Map<String, List<Catelog2Vo>> getCatalogJsonFromDb(){
        //将数据库的多次查询变为一次
        List<CategoryEntity> selectList = this.baseMapper.selectList(null);
        //1、查出所有分类
        //1、1）查出所有一级分类
        List<CategoryEntity> level1Categorys = getParent_cid(selectList, 0L);
        //封装数据
        return level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            //1、每一个的一级分类,查到这个一级分类的二级分类
            List<CategoryEntity> categoryEntities = getParent_cid(selectList, v.getCatId());

            //2、封装上面的结果
            List<Catelog2Vo> catelog2Vos = new ArrayList<>();
            if (categoryEntities != null) {
                catelog2Vos = categoryEntities.stream().map(l2 -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());

                    //1、找当前二级分类的三级分类封装成vo
                    List<CategoryEntity> level3Catelog = getParent_cid(selectList, l2.getCatId());

                    if (level3Catelog != null) {
                        List<Catelog2Vo.Catalog3Vo> category3Vos = level3Catelog.stream().map(l3 -> {
                            //2、封装成指定格式
                            return new Catelog2Vo.Catalog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                        }).collect(Collectors.toList());
                        catelog2Vo.setCatalog3List(category3Vos);
                    }

                    return catelog2Vo;
                }).collect(Collectors.toList());
            }
            return catelog2Vos;
        }));
    }

    private Map<String, List<Catelog2Vo>> getDataFromDb(){
        // 首先去查缓存是否命中
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        // 加入缓存
        String catalogJSON = ops.get("catalogJSON");
        // 命中则直接返回
        if(StringUtils.isNotEmpty(catalogJSON)){
            return JSON.parseObject(catalogJSON, new TypeReference<Map<String, List<Catelog2Vo>>>() {});
        }
        // 未命中则走数据库
        Map<String, List<Catelog2Vo>> res = getCatalogJsonFromDb();
        String jsonString = JSON.toJSONString(res);
        if(StringUtils.isNotEmpty(jsonString)){
            // 放入redis缓存
            ops.set("catalogJSON", jsonString);
        }
        return res;
    }
    private Map<String, List<Catelog2Vo>> getCatalogJsonFromDbWithLocalLock() {
        Map<String, List<Catelog2Vo>> res = null;
        // 加锁
        try {
            lock.lock();
            return getDataFromDb();
        }catch (Exception ignored){
            // 异常处理
        }finally {
            lock.unlock();
        }
        return res;
    }

    private Map<String, List<Catelog2Vo>> getCatalogJsonFromDbWithRedissonLock() {
        RLock lock = redissonClient.getLock("catalogJson-lock");
        lock.lock();
        Map<String, List<Catelog2Vo>> res = null;
        try {
            res = getDataFromDb();
        }finally {
            lock.unlock();
        }
        return res;
    }
    private Map<String, List<Catelog2Vo>> getCatalogJsonFromDbWithRedisLock() {
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        String uuid = UUID.randomUUID().toString();
        // 加锁和设置过期时间为原子操作
        Boolean lock1 = ops.setIfAbsent("lock", uuid, 300, TimeUnit.SECONDS);// 设置300s过期时间
        if(Boolean.TRUE.equals(lock1)){ // 如果设置成功 则加锁成功
            System.out.println("获得分布式锁成功");
            Map<String, List<Catelog2Vo>> res;
            try {
                res = getDataFromDb();
            }finally {
                // 业务执行完毕后，直接解锁
                String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                // 解锁
                stringRedisTemplate.execute(new DefaultRedisScript<>(script, Long.class), Collections.singletonList("lock"), uuid);
            }
            return res;
        }else{
            // 获取锁失败
            System.out.println("获得分布式锁失败，正在重试...");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return getCatalogJsonFromDbWithRedisLock();
        }
    }




    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJson() {
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        // 加入缓存
        String catalogJSON = ops.get("catalogJSON");
        if (StringUtils.isEmpty(catalogJSON)) {
            // 缓存没有数据 ，则从数据库中查询并放入
            return getCatalogJsonFromDbWithRedissonLock();
        }
        return JSON.parseObject(catalogJSON, new TypeReference<Map<String, List<Catelog2Vo>>>() {});
    }


    private void getcateLogPaths(List<Long> paths, Long catelogId) {
        paths.add(catelogId);
        Long parentCid = pmsCategoryDao.selectById(catelogId).getParentCid();
        if (parentCid != 0) {
            getcateLogPaths(paths, parentCid);
        }
    }

    private List<CategoryEntity> getChildrens(CategoryEntity root, List<CategoryEntity> all) {
        return all.stream().filter((menu) -> {
            return Objects.equals(menu.getParentCid(), root.getCatId()); // 就是菜单的父菜单等于root的菜单id 那么就是root的子菜单
        }).peek((pmsCategoryEntity) -> {//找到children后继续找children
            pmsCategoryEntity.setChildren(getChildrens(pmsCategoryEntity, all)); // 继续递归设置
        }).collect(Collectors.toList());
    }

    private List<CategoryEntity> getParent_cid(List<CategoryEntity> selectList, Long parentCid) {
        return selectList.stream().filter(item -> item.getParentCid().equals(parentCid)).collect(Collectors.toList());
    }

}