package com.xyj.gulimal.gulimallsearch.service;

import com.xyj.gulimal.gulimallsearch.vo.SearchParam;
import com.xyj.gulimal.gulimallsearch.vo.SearchResult;

import java.io.IOException;

/**
 * @Author jie
 * @Date 2023/7/28 14:34
 */
public interface MallSearchService {
    /**
     * 根据页面提交的请求参数，得到所查询的结果
     * @param searchParam
     * @return
     */
    SearchResult search(SearchParam searchParam) throws IOException;
}
