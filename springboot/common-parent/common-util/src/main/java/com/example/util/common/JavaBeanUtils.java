package com.example.util.common;

import com.google.gson.Gson;

import java.util.LinkedHashMap;
import java.util.Map;

public class JavaBeanUtils {

    private static final Gson GSON = new Gson();

    public static Map beanToMap(Object obj) {
        return GSON.fromJson(GSON.toJson(obj), LinkedHashMap.class);
    }

    public static <T> T mapToBean(Map map,Class<T> clazz) {
        return GSON.fromJson(GSON.toJson(map), clazz);
    }



}
