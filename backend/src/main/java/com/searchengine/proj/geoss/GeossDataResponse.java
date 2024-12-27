package com.searchengine.proj.geoss;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeossDataResponse {
    private int httpStatus;
    private String message;
    private List<GeossDataContainer> responseBody;

    @Data
    public class GeossDataContainer {
        private String id;
    }
}
