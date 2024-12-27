package com.searchengine.proj.tpdc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TPDCResponse {
    private String code;
    private String message;
    private Context context;

    @Data
    public class Context{
        private MetaData metadataViewVOPage;
    }

    @Data
    public class MetaData {
        private List<Records> records;
    }

    @Data
    public class Records {
        private String id;
        private String title;
        private String description;
        private String tsCreated;
        private String tsUpdated;
        private String authorName;
        private int download;
        private int hitCount;
    }
}
