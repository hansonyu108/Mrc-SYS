package com.searchengine.proj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class QualityFactor {
    private Double spatialRelevance;
    private Double temporalRelevance;
    private int visit;
    private int download;
    private String commitDate;
    private String title;
    private String description;
    private String language;
    private List<String> keyword;
    private List<String> theme;
    private List<String> subject;
    private String timeResolution;
    private String timeRange;
    private String updateType;
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
    private String dataSource;
    private String dataProcess;
    private String api;
    private String url;
    public String toCsvRow() {
        StringJoiner joiner = new StringJoiner(",");

//        joiner.add(spatialRelevance == null ? "" : spatialRelevance.toString());
//        joiner.add(temporalRelevance == null ? "" : temporalRelevance.toString());
        joiner.add(visit == 0 ? "0" : String.valueOf(visit));
        joiner.add(download == 0 ? "0" : String.valueOf(download));
        joiner.add(commitDate == null ? "" : escapeCsvField (commitDate));
        joiner.add(title == null ? "" : title);
        joiner.add(description == null ? "" : escapeCsvField(description.replaceAll("\\r\\n|\\r|\\n", "")));
        joiner.add(language == null ? "" : language.trim());
        joiner.add(keyword == null ? "" : listToString(keyword));
        joiner.add(theme == null ? "" : listToString(theme));
        joiner.add(subject == null ? "" : listToString(subject));
        joiner.add(timeResolution == null ? "" : timeResolution);
        joiner.add(timeRange == null ? "" : timeRange);
        joiner.add(updateType == null ? "" : updateType);
        joiner.add(projectInfo == null ? "" : escapeCsvField(projectInfo.replaceAll("\\r\\n|\\r|\\n", "")));
        joiner.add(spatialLocation == null ? "" : escapeCsvField(spatialLocation.replaceAll("\\r\\n|\\r|\\n", "")));
        joiner.add(scale == null ? "" : scale);
        joiner.add(westLng == null ? "" : westLng);
        joiner.add(northLat == null ? "" : northLat);
        joiner.add(eastLng == null ? "" : eastLng);
        joiner.add(southLat == null ? "" : southLat);
        joiner.add(mapCenterLng == null ? "" : mapCenterLng.toString());
        joiner.add(mapCenterLat == null ? "" : mapCenterLat.toString());
        joiner.add(mapZoom == 0 ? "0" : String.valueOf(mapZoom));
        joiner.add(dataSource == null ? "" : escapeCsvField(dataSource.replaceAll("\\r\\n|\\r|\\n", "")));
        joiner.add(dataProcess == null ? "" : escapeCsvField(dataProcess.replaceAll("\\r\\n|\\r|\\n", "")));
        joiner.add(url == null ? "" : escapeCsvField(url.replaceAll("\\r\\n|\\r|\\n", "")));

        return joiner.toString();
    }

    // 将List转为单行字符串，使用"; "分隔
    private String listToString(List<String> list) {
        list.removeIf(Objects::isNull);
        List<String> collect = list.stream().map(String::trim).collect(Collectors.toList());
        return collect == null ? "" : escapeCsvField(String.join("; ", collect));
    }

    private static String escapeCsvField(String field) {
        // 如果字段包含逗号或引号，则需要用双引号包裹
//        if (field.contains(",") || field.contains("\"")) {
            // 转义引号
        field = field.replace("\"", "\"\"");
        return "\"" + field + "\"";
//        }
//        return field;
    }
}
