package com.example.util.http.callback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
/**
 * @description: 实现http异步返回的接口 泛型为必传
 * @author:      CL
 * @date:        2018/10/12 9:23
 * @version:     1.0
*/
public abstract class HttpResponseCallback<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpResponseCallback.class);
    /**
     * 成功时候的回调
     *
     * @param t
     */
    public abstract void success(T t);

    /**
     * 可覆盖
     * @param e
     */
    public void failed(Exception e) {
        LOGGER.error("异步http请求发生错误!", e);
    }
    /*
     * @return 返回T相应的class类型
     */
    public Type getType(){
        return ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

}
