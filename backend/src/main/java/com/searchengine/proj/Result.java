package com.searchengine.proj;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> {
    private Integer code;
    private String msg;
    private T data;

    public static <T> Result<T> success(T data){
        return new Result<T>(200, "请求成功", data);
    }
    public static <T> Result<T> failure(int code, String message) {
        return new Result<>(code, message, null);
    }
}
