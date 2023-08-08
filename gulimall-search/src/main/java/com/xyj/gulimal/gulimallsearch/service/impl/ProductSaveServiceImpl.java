package com.xyj.gulimal.gulimallsearch.service.impl;

import com.alibaba.fastjson.JSON;
import com.xyj.common.to.es.SkuEsModel;
import com.xyj.gulimal.gulimallsearch.constant.EsConstant;
import com.xyj.gulimal.gulimallsearch.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.xyj.gulimal.gulimallsearch.config.GulimallElasticsearchConfig.COMMON_OPTIONS;

/**
 * @Author jie}
 * @Date 2023/6/25 23:34}
 */
@Slf4j
@Service
public class ProductSaveServiceImpl implements ProductSaveService {
    @Autowired
    RestHighLevelClient restHighLevelClient;
    @Override
    public boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException {
        // 构造商品保存
        BulkRequest bulkRequest = new BulkRequest();
        for(SkuEsModel model : skuEsModels){
            IndexRequest indexRequest = new IndexRequest();
            // 找索引表
            indexRequest.index(EsConstant.PRODUCT_INDEX);
            // 因为skuId是唯一的，因此直接将其作为索引id
            indexRequest.id(model.getSkuId().toString());
            // 将实体类变为json数据
            String jsonString = JSON.toJSONString(model);
            // 提交到ES需要使用JSON数据
            indexRequest.source(jsonString, XContentType.JSON);
            bulkRequest.add(indexRequest);
        }
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, COMMON_OPTIONS);
        // 如果批量错误 可以进行处理
        if(bulk.hasFailures()){
            List<String> list = Arrays.stream(bulk.getItems()).map(BulkItemResponse::getId).collect(Collectors.toList());
            log.error("商品上架出错:{}", list);
            return false;
        }
        return true;
    }
}
