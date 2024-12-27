package com.searchengine.proj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SearchRecommendation {
    private String id;
    private int visit;
    private int download;
    private Double Relevance;
    private String title;
    private String description;
    private String date;
    private String institution;
    private String url;
    private String author;
}
