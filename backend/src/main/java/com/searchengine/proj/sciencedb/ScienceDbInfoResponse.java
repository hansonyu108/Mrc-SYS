package com.searchengine.proj.sciencedb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ScienceDbInfoResponse {
    private int code;
    private String message;
    private DataContent data;

    @Data
    public class DataContent {
        private InfoContent Content;
    }

    @Data
    public class InfoContent{
        private String dataSetId;
        private String titleZh;
        private String introductionZh;
        private String visit;
        private String download;
        private Long dataSetCreateDate;
        private String publisher;
        private List<Taxonomy> taxonomy;
        private List<String> keywordZh;
    }


    @Data
    public class Taxonomy {
        private String nameZh;
    }
}
