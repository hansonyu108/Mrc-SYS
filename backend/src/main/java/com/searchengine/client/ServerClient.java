package com.searchengine.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient("flask-faiss")
public interface ServerClient {
    @PostMapping("/faiss")
    List<Double> getCosineSimilarity(@RequestBody Map<String, String> requestBody);
}
