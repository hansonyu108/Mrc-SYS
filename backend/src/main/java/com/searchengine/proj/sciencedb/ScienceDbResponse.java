package com.searchengine.proj.sciencedb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScienceDbResponse {
    private Integer code;
    private String message;
    private ScienceDbDataContainer data;
    @Data
    public class ScienceDbDataContainer{
        private List<Object> fileTypeList;
        private List<ScienceDbRecommendation> data;
    }
    @Data
    public class ScienceDbRecommendation{
        private List<String> keywordZh;
        private List<Author> author;
        private String introductionZh;
        private String dataSetPublishDate;
        private String titleZh;
        private String dataSetId;
        private String version;
        private String id;
    }
    @Data
    public class Author {
        private String nameZh;
    }
}
