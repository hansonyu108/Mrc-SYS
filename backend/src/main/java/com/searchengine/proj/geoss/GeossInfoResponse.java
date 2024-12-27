package com.searchengine.proj.geoss;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeossInfoResponse {
    private int httpStatus;
    private String message;
    private DateDetail responseBody;

    @Data
    public static class DateDetail{
        private String id;
        private int visit;
        private int download;
        private CopyRight copyRight;
        private String commitDate;
        private String title;
        private String description;
        private String language;
        private List<String> keyword;
        private Category category;
        private TimeInfo timeInfo;
        private SpatialLocation spatialLocation;
        private DataSource dataSource;
        private Contributor contributor;


    }

    @Data
    public static class CopyRight{
        private boolean redistributeAuthorize;
        private String redistributeArea;
        private boolean applyDelayedDisclosure;
        private String protectTime;
        private String dataLevel;
        private String dataReference;
        private String offerPromise;
        private String dataSharingMod;
    }

    @Data
    public static class Category{
        private List<String> categorySubject;
        private List<String> categoryTheme;
    }

    @Data
    public static class TimeInfo{
        private String timeResolution;
        private String timeRange;
        private String updateType;
        private String updatePeriod;
    }

    @Data
    public static class SpatialLocation{
        private String projectInfo;
        private String spatialLocation;
        private String scale;
        private String westLng;
        private String northLat;
        private String eastLng;
        private String southLat;
        private Double mapCenterLng;
        private Double mapCenterLat;
        private int mapZoom;

    }

    @Data
    public static class DataSource {
        private String dataSource;
        private String dataProcess;

    }

    @Data
    public static class Contributor {
        private String fullName;
        private String contributorUnitName;
        private String contributorAddress;

    }
}
