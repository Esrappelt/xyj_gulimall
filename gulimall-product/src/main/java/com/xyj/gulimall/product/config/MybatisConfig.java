package com.xyj.gulimall.product.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
/*
* 定义拦截器 在where xxx  limit ?,? 可以自己设置
* */
@Configuration
@EnableTransactionManagement
@MapperScan(basePackages = "com.xyj.gulimall.product.dao")
public class MybatisConfig {
    @Bean
    public PaginationInterceptor paginationInterceptor(){
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        paginationInterceptor.setLimit(1000); // limit 0, 1000
        paginationInterceptor.setOverflow(true);// 超出最大页数范围，返回首页
        return paginationInterceptor;
    }
}
