package com.example.util.http;

import com.alibaba.fastjson.JSON;
import com.example.util.common.Response;
import com.example.util.http.callback.HttpResponseCallback;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 两种使用方法
 * 1 建议使用多例 然后注入的形式
 *
 * @Bean
 * @Scope("prototype") public AsyncHttpRestUtils asyncHttpRestUtils(){
 * return AsyncHttpRestUtils.getDefaultInstance();
 * }
 * @Autowired private AsyncHttpRestUtils asyncHttpRestUtils;
 * 2 如果没有spring则直接在成员变量上
 * private static final AsyncHttpRestUtils ASYNC_HTTP_REST_UTILS=AsyncHttpRestUtils.getDefaultInstance();
 * <p>
 * <p>
 * 这里需要注意的地方：不要在每个方法实例化一封这样会造成资源浪费
 */
public class AsyncHttpRestUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncHttpRestUtils.class);

    private static final Gson GSON = new Gson();
    private static final String CHARSET = "UTF-8";

    private final CloseableHttpAsyncClient closeableHttpAsyncClient;


    private AsyncHttpRestUtils() {
        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setSocketTimeout(20000)
                .setConnectTimeout(20000)
                .setConnectionRequestTimeout(20000)
                .build();
        closeableHttpAsyncClient = HttpAsyncClients.custom().setDefaultRequestConfig(defaultRequestConfig).setMaxConnPerRoute(100).setMaxConnTotal(500).build();
        // Start the client
        closeableHttpAsyncClient.start();


        LOGGER.info("异步httpClient初始化完成");
    }

    private AsyncHttpRestUtils(CloseableHttpAsyncClient closeableHttpAsyncClient) {
        this.closeableHttpAsyncClient = closeableHttpAsyncClient;
        if (!this.closeableHttpAsyncClient.isRunning()) {
            closeableHttpAsyncClient.start();
        }
        LOGGER.info("异步httpClient初始化完成");
    }

    /**
     * 得到默认实例
     *
     * @return
     */
    public static AsyncHttpRestUtils getDefaultInstance() {
        return new AsyncHttpRestUtils();
    }

    /**
     * 得到自定义实例
     *
     * @param closeableHttpAsyncClient
     * @return
     */
    public static AsyncHttpRestUtils getInstance(CloseableHttpAsyncClient closeableHttpAsyncClient) {
        return new AsyncHttpRestUtils(closeableHttpAsyncClient);
    }

    private static final AsyncHttpRestUtils ASYNC_HTTP_REST_UTILS = AsyncHttpRestUtils.getDefaultInstance();

    public static void main(String[] args) throws InterruptedException {
        AtomicInteger success = new AtomicInteger();

        int j = 0;
        long l = System.currentTimeMillis();
        for (int i = 0; i < 1; i++) {
            ASYNC_HTTP_REST_UTILS.get("http://localhost:8082/sys/getPublicKey", null, null, new HttpResponseCallback<Response<Map<String,String>>>() {

                @Override
                public void success(Response<Map<String, String>> mapResponse) {

                    System.out.println(JSON.toJSON(mapResponse));
                }
            });
        }
    }

    public <T> void get(String url,
                        List<NameValuePair> params,
                        Map<String, String> headers,
                        HttpResponseCallback<T> callback
    ) {
        buildAndSendRequest("GET", url, params, headers, callback);
    }

    public <T> void post(String url,
                         Object params,
                         Map<String, String> headers,
                         HttpResponseCallback<T> callback
    ) {
        buildAndSendRequest("POST", url, params, headers, callback);
    }

    public <T> void put(String url,
                        Object params,
                        Map<String, String> headers,
                        HttpResponseCallback<T> callback
    ) {
        buildAndSendRequest("PUT", url, params, headers, callback);
    }

    public <T> void delete(String url,
                           Object params,
                           Map<String, String> headers,
                           HttpResponseCallback<T> callback
    ) {
        buildAndSendRequest("DELETE", url, params, headers, callback);
    }


    /**
     * 构建
     *
     * @param method   方法GET POST PUT DELETE
     * @param url
     * @param params
     * @param headers
     * @param callback
     * @param <T>
     */
    private <T> void buildAndSendRequest(String method,
                                         String url,
                                         Object params,
                                         Map<String, String> headers,
                                         HttpResponseCallback<T> callback
    ) {
        HttpRequestBase request = null;
        if (Objects.equals("GET", method)) {

            List<NameValuePair> getParams = (List<NameValuePair>) params;

            if (getParams != null && getParams.size() > 0) {
                StringBuilder sb = new StringBuilder();
                for (NameValuePair entry : getParams) {
                    sb.append("&").append(entry.getName()).append("=").append(entry.getValue());
                }
                if (url.contains("?")) {
                    url = url + sb.toString();
                } else {
                    url = "?" + url + sb.toString();
                }
            }
            request = new HttpGet(url);
            headers = headers == null ? new HashMap<String, String>() : headers;

            headers.put("Content-Type", "application/json");
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                request.setHeader(entry.getKey(), entry.getValue());
            }
        } else if (Objects.equals("POST", method)) {
            request = new HttpPost(url);
            if (params != null) {
                ((HttpPost) request).setEntity(new StringEntity(params instanceof String ? params.toString() : GSON.toJson(params), CHARSET));
            }
            headers = headers == null ? new HashMap<String, String>() : headers;

            headers.put("Content-Type", "application/json");
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                request.setHeader(entry.getKey(), entry.getValue());
            }
        } else if (Objects.equals("PUT", method)) {
            request = new HttpPut(url);
            if (params != null) {
                ((HttpPut) request).setEntity(new StringEntity(params instanceof String ? params.toString() : GSON.toJson(params), CHARSET));
            }
            headers = headers == null ? new HashMap<String, String>() : headers;

            headers.put("Content-Type", "application/json");
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                request.setHeader(entry.getKey(), entry.getValue());
            }
        } else if (Objects.equals("DELETE", method)) {
            request = new HttpDeleteWithBody(url);
            if (params != null) {
                ((HttpDeleteWithBody) request).setEntity(new StringEntity(params instanceof String ? params.toString() : GSON.toJson(params), CHARSET));
            }
            headers = headers == null ? new HashMap<String, String>() : headers;

            headers.put("Content-Type", "application/json");
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                request.setHeader(entry.getKey(), entry.getValue());
            }
        }
        if (request != null) {
            closeableHttpAsyncClient.execute(request, new FutureCallback<HttpResponse>() {
                @Override
                public void completed(HttpResponse result) {
                    if (callback == null) {
                        return;
                    }
                    try {
                        String json = EntityUtils.toString(result.getEntity(), CHARSET);
                        if (callback.getType() == Object.class || callback.getType() == String.class
                                || callback.getType() == void.class || callback.getType() == Void.class
                        ) {
                            callback.success((T) json);
                        } else {
                            callback.success(GSON.fromJson(json, callback.getType()));
                        }
                    } catch (IOException e) {
                        callback.failed(e);
                    }


                }

                @Override
                public void failed(Exception ex) {
                    callback.failed(ex);
                }

                @Override
                public void cancelled() {
                    callback.failed(new Exception("http请求取消"));
                }
            });

        }


    }

    @NotThreadSafe
    static class HttpDeleteWithBody extends HttpEntityEnclosingRequestBase {
        public static final String METHOD_NAME = "DELETE";

        @Override
        public String getMethod() {
            return METHOD_NAME;
        }

        public HttpDeleteWithBody(final String uri) {
            super();
            setURI(URI.create(uri));
        }

        public HttpDeleteWithBody(final URI uri) {
            super();
            setURI(uri);
        }

        public HttpDeleteWithBody() {
            super();
        }
    }




}
