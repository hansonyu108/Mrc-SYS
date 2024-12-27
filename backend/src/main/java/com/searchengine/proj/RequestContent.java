package com.searchengine.proj;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "检索数据时的数据模型")
public class RequestContent {
    @Schema(description = "检索内容")
    private String inputContent;

    @Schema(description = "数据类型")
    private List<String> dataType;

    @Schema(description = "空间范围")
    private String spatialExtent;

    @Schema(description = "数据主题")
    private String topic;

    @Schema(description = "起始时间")
    private String dateStart;

    @Schema(description = "终止时间")
    private String dateEnd;

    @Schema(description = "页码")
    private Integer page = 1;

    @Schema(description = "一页数据数")
    private Integer size = 10;
}
