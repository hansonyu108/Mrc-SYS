package com.searchengine.exception;


public class HttpException extends RuntimeException{
    public HttpException(){

    }
    public HttpException(String msg){
        super(msg);
    }
}
