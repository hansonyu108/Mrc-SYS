package com.searchengine.proj.flask;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class FaissResponse {
    private List<Double> data;
}
