package com.xyj.gulimall.product.config;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author jie
 * @Date 2023/7/29 15:58
 */
@Component
@ConfigurationProperties(prefix = "gulimall.thread")
@Data
@ToString
public class ThreadPoolConfigProperties {
    private Integer coreSize;
    private Integer maxSize;
    private Integer keepAliveTime;
}
