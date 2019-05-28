package com.example.util.common;

import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

public class Response<R> {
    @Getter
    private static final int SUCCESS_CODE = 1;
    @Getter
    private static final int ERROR_CODE = 0;

    @Getter
    @Setter
    public int code;
    @Getter
    @Setter
    private String message;
    @Getter
    @Setter
    private R data;

    private Response() {
    }

    public static Response ok() {
        Response response = new Response<>();
        response.code = SUCCESS_CODE;
        return response;
    }

    public static Response error(String message) {
        Response response = new Response<>();
        response.code = ERROR_CODE;
        response.message = message;
        return response;
    }

    public static Response wrap(int code, String message) {
        Response response = new Response<>();
        response.code = code;
        response.message = message;
        return response;
    }

    public Response wrap(R data) {
        Response response = Response.ok();
        response.data = data;
        return response;
    }

    public Response wrap(int code, String message, R data) {
        Response response = new Response<>();
        response.code = code;
        response.message = message;
        response.data = data;
        return response;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
