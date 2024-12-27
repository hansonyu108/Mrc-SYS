package com.searchengine.controller;

import com.searchengine.proj.*;
import com.searchengine.service.imp.SearchImp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Tag(name = "数据集成检索接口", description = "包含GEOSS和TPDC")
public class SearchController {
    private final SearchImp search;
    @GetMapping("/search")
    @Operation(summary = "获取检索数据列表")
    public Result search(RequestContent content) throws Exception{
        List<SearchRecommendation> recommendations = search.combinedSearch(content);

        return Result.success(recommendations);
    }


    @GetMapping("/getDataInfo")
    @Operation(summary = "获取数据详情数据")
    public Result getInfo(InfoRequestContent content){
        QualityFactor qualityFactor = search.getInfo(content);
        return Result.success(qualityFactor);
    }
}
