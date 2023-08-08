package com.xyj.gulimall.member;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
// 远程调用别人的服务
// 需要引入OpenFeign
// 编写一个接口，告诉Springcloud这个接口需要调用什么服务
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class MemberApplication {
    public static void main(String[] args) {
        SpringApplication.run(MemberApplication.class, args);
    }
}
