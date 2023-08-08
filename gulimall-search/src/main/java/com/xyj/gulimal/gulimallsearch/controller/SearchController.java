package com.xyj.gulimal.gulimallsearch.controller;

import com.xyj.gulimal.gulimallsearch.service.MallSearchService;
import com.xyj.gulimal.gulimallsearch.vo.SearchParam;
import com.xyj.gulimal.gulimallsearch.vo.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

/**
 * @Author jie
 * @Date 2023/7/28 14:18
 */
@Controller
public class SearchController {
    @Autowired
    MallSearchService mallSearchService;
    @GetMapping("/list.html")
    public String listPage(SearchParam searchParam, Model model) throws IOException {
        SearchResult search = mallSearchService.search(searchParam);
        model.addAttribute("result", search);
        return "list";
    }
}
