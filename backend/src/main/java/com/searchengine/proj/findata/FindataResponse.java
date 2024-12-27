package com.searchengine.proj.findata;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class FindataResponse {
    private int code;
    private String message;
    private FindataDataContainer  data;

    @Data
    public class FindataDataContainer{
        private List<FindataRecommendation> recommendData;
    }
    @Data
    public class FindataRecommendation{
        @SerializedName("yi")
        private String title;

        @SerializedName("bing")
        private String bing;

        @SerializedName("ding")
        private String description;

        @SerializedName("wu")
        private String date;

        @SerializedName("ji")
        private String year;

        @SerializedName("geng")
        private String geng;

        @SerializedName("ren")
        private String ren;

        @SerializedName("gui")
        private String institution;

        @SerializedName("chen")
        private String url;

        @SerializedName("shen")
        private int shen;

        @SerializedName("you")
        private List<String> you;

        @SerializedName("three")
        private List<Object> three;

        @SerializedName("xin")
        private String author;
    }
}
