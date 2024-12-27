package com.searchengine.proj.tpdc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class TPDCInfoResponse {
    private DataContext context;

    @Data
    public class DataContext {
        private MetadataVO metadataVO;
        private List<Author> author;
        private List<KeywordStand> keywordStandVOList;
        private List<PlaceKeyword> placeKeywordVOList;
        private List<Theme> themeList;
    }

    @Data
    public class MetadataVO {
        private String id;
        private String realName;
        private String title;
        private String description;
        private String instructions;
        private String projection;
        private String tsUpdated;
        private String spatialResolution;
        private String temporalResolution;
        private String timeDescription;
        private String tsPublish;
        private String language;
        private Double east;
        private Double west;
        private Double south;
        private Double north;
        private Long scale;
        private int download;
        private int hitCount;

    }

    @Data
    public class Author {
        private String name;
    }

    @Data
    public class KeywordStand {
        private String name;
    }

    @Data
    public class Theme {
        private String name;
    }

    @Data
    public class PlaceKeyword {
        private String keyword;
    }

}
