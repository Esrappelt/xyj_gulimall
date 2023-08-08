package com.xyj.gulimal.gulimallsearch;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonParser;
import lombok.Data;
import lombok.ToString;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static com.xyj.gulimal.gulimallsearch.config.GulimallElasticsearchConfig.COMMON_OPTIONS;


// Es Test
@SpringBootTest
class GulimallSearchApplicationTests {
    @Autowired
    RestHighLevelClient restClient;
    @Test
    void contextLoads() {
        System.out.println(restClient);
    }

    @Data
    @ToString
    static class Account {

        private int account_number;
        private int balance;
        private String firstname;
        private String lastname;
        private int age;
        private String gender;
        private String address;
        private String employer;
        private String email;
        private String city;
        private String state;
    }
    @Test
    void index_data() throws IOException {
        @Data
        class User{
            private String userName;
            private String gender;
            private Integer age;
        }
        IndexRequest indexRequest = new IndexRequest("users");
        User user = new User();
        user.setAge(18);
        user.setUserName("张三");
        user.setGender("男");
        String jsonString = JSON.toJSONString(user);
        indexRequest.id("1");
        indexRequest.source(jsonString, XContentType.JSON);

        IndexResponse response = restClient.index(indexRequest, COMMON_OPTIONS);
        System.out.println(response);
    }

    @Test
    void searchData() throws IOException {
        SearchRequest searchRequest = new SearchRequest();
        // 检索表指定
        searchRequest.indices("bank");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchRequest.source(searchSourceBuilder);

        //检索参数
        searchSourceBuilder.query(QueryBuilders.matchQuery("address", "mill"));
        TermsAggregationBuilder ageAgg = AggregationBuilders.terms("ageAgg").field("age").size(10);
        searchSourceBuilder.aggregation(ageAgg);

        AvgAggregationBuilder balanceAvg = AggregationBuilders.avg("balanceAvg").field("age");
        searchSourceBuilder.aggregation(balanceAvg);
        System.out.println(searchSourceBuilder);

        // 执行检索
        SearchResponse searchResponse = restClient.search(searchRequest, COMMON_OPTIONS);
        System.out.println(searchResponse);
        SearchHit[] searchHits = searchResponse.getHits().getHits();
        for(SearchHit hit : searchHits){
            System.out.println(hit);
            String sourceAsString = hit.getSourceAsString();
            Account account = JSON.parseObject(sourceAsString, Account.class);
            System.out.println("acount:" + account);
        }
    }

}
