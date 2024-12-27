package com.searchengine.service;

import com.searchengine.proj.InfoRequestContent;
import com.searchengine.proj.QualityFactor;
import com.searchengine.proj.RequestContent;
import com.searchengine.proj.SearchRecommendation;

import java.util.List;

public interface Search {
     List<SearchRecommendation> combinedSearch(RequestContent content);
     QualityFactor getInfo(InfoRequestContent content);
}
