package com.xyj.gulimal.gulimallsearch.service;

import com.xyj.common.to.es.SkuEsModel;

import java.io.IOException;
import java.util.List;

/**
 * @Author jie}
 * @Date 2023/6/25 23:32}
 */

public interface ProductSaveService {
    boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException;
}
