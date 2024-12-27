package com.searchengine.utils;

public class MathUtil {
    public static Double getRangValue(Double low, Double high){
        Double randomValue = low + (Math.random() * (high - low));
        Double roundedValue = Math.round(randomValue * 10) / 10.0;
        return roundedValue;
    }

    public static Double scoreFormat(Double similarity){
        return similarity * 5.0;
    }
}
