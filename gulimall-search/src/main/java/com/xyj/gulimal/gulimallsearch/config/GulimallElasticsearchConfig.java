package com.xyj.gulimal.gulimallsearch.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author jie}
 * @Date 2023/6/24 20:30}
 */
@Configuration
public class GulimallElasticsearchConfig {

    public static final RequestOptions COMMON_OPTIONS;
    static {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
        COMMON_OPTIONS = builder.build();
    }

    @Bean
    RestHighLevelClient esRestClient(){
        RestClientBuilder buidler = RestClient.builder(new HttpHost("192.168.28.101", 9200,"http"));
        return new RestHighLevelClient(buidler);
    }
}
