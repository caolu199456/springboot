package com.example.util.http;


import com.google.gson.Gson;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 两种使用方法
 * 1 建议使用多例 然后注入的形式
 *
 * @Bean
 * @Scope("prototype") public HttpRestUtils httpRestUtils(){
 * return HttpRestUtils.getDefaultInstance();
 * }
 * @Autowired private HttpRestUtils httpRestUtils;
 * 2 如果没有spring则直接在成员变量上
 * private static final HttpRestUtils HTTP_REST_UTILS=HttpRestUtils.getDefaultInstance();
 * <p>
 * <p>
 * 这里需要注意的地方：不要在每个方法实例化一封这样会造成资源浪费
 */
public class HttpRestUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpRestUtils.class);

    private static final String CHARSET = "UTF-8";

    private static final Gson GSON = new Gson();

    private final HttpClient httpClient;


    private HttpRestUtils() {
        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setSocketTimeout(20000)
                .setConnectTimeout(20000)
                .setConnectionRequestTimeout(20000)
                .build();
        httpClient = HttpClients.custom()
                .setDefaultRequestConfig(defaultRequestConfig)
                .setMaxConnTotal(5000)
                .setMaxConnPerRoute(100)
                //重试1次
                .setRetryHandler(new DefaultHttpRequestRetryHandler(5, false))
                .build();
        LOGGER.info("httpClient初始化完成");
    }

    private HttpRestUtils(HttpClient httpClient) {
        this.httpClient = httpClient;
        LOGGER.info("httpClient初始化完成");
    }

    /**
     * 得到默认实例
     *
     * @return
     */
    public static HttpRestUtils getDefaultInstance() {
        return new HttpRestUtils();
    }

    /**
     * 得到自定义实例
     *
     * @param httpClient
     * @return
     */
    public static HttpRestUtils getInstance(HttpClient httpClient) {
        return new HttpRestUtils(httpClient);
    }

    private static final HttpRestUtils HTTP_REST_UTILS = HttpRestUtils.getDefaultInstance();

    public static void main(String[] args) {
        long l = System.currentTimeMillis();
        Pair<Integer, String> integerPair = HTTP_REST_UTILS.get("http://wctestapi.freechargechina.com:8011//coreExpose/", null, null, String.class);

        System.out.println(integerPair.getValue());

        System.out.println("..." + (System.currentTimeMillis() - l));

    }

    /**
     * @param url
     * @param params
     * @param headers
     * @param type List.class或者 new TypeToken<Response<HashMap<Integer,String>>>(){}.getType()
     * @param <T>
     * @return
     */
    public <T> Pair<Integer, T> get(String url,
                                    List<NameValuePair> params,
                                    Map<String, String> headers,
                                    Type type
    ) {
        HttpResponse response = null;
        try {
            if (params != null && params.size() > 0) {
                StringBuilder sb = new StringBuilder();
                for (NameValuePair entry : params) {
                    sb.append("&").append(entry.getName()).append("=").append(entry.getValue());
                }
                if (url.contains("?")) {
                    url = url + sb.toString();
                } else {
                    url = "?" + url + sb.toString();
                }
            }
            HttpGet httpGet = new HttpGet(url);
            headers = headers == null ? new HashMap<String, String>() : headers;

            headers.put("Content-Type", "application/json");
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpGet.setHeader(entry.getKey(), entry.getValue());
            }

            response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity, CHARSET);

            T t = null;
            if (type ==null || type == String.class || type == Object.class || type == void.class || type==Void.class) {
                t = (T)result;
            }else {
                t = GSON.fromJson(result, type);
            }
            return  Pair.of(response.getStatusLine().getStatusCode(), t);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (response instanceof Closeable) {
                try {
                    ((Closeable) response).close();
                } catch (IOException e) {
                }
            }
        }
    }

    public <T> Pair<Integer, T> post(String url,
                                     Object params,
                                     Map<String, String> headers,
                                     Type type) {
        HttpResponse response = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            if (params != null) {
                httpPost.setEntity(new StringEntity(params instanceof String ? params.toString() : GSON.toJson(params), CHARSET));
            }
            headers = headers == null ? new HashMap<String, String>() : headers;

            headers.put("Content-Type", "application/json");
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpPost.setHeader(entry.getKey(), entry.getValue());
            }
            response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity, CHARSET);

            T t = null;
            if (type ==null || type == String.class || type == Object.class || type == void.class || type==Void.class) {
                t = (T)result;
            }else {
                t = GSON.fromJson(result, type);
            }
            return Pair.of(response.getStatusLine().getStatusCode(), t);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (response instanceof Closeable) {
                try {
                    ((Closeable) response).close();
                } catch (IOException e) {
                }
            }
        }
    }

    public <T> Pair<Integer, T> put(String url,
                                    Object params,
                                    Map<String, String> headers,
                                    Type type) {
        HttpResponse response = null;
        try {
            HttpPut httpPut = new HttpPut(url);
            if (params != null) {
                httpPut.setEntity(new StringEntity(params instanceof String ? params.toString() : GSON.toJson(params), CHARSET));
            }
            headers = headers == null ? new HashMap<String, String>() : headers;

            headers.put("Content-Type", "application/json");
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpPut.setHeader(entry.getKey(), entry.getValue());
            }
            response = httpClient.execute(httpPut);
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity, CHARSET);

            T t = null;
            if (type ==null || type == String.class || type == Object.class || type == void.class  || type==Void.class) {
                t = (T)result;
            }else {
                t = GSON.fromJson(result, type);
            }
            return Pair.of(response.getStatusLine().getStatusCode(), t);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (response instanceof Closeable) {
                try {
                    ((Closeable) response).close();
                } catch (IOException e) {
                }
            }
        }
    }

    public <T> Pair<Integer, T> delete(String url,
                                       Object params,
                                       Map<String, String> headers,
                                       Type type) {
        HttpResponse response = null;
        try {
            HttpDeleteWithBody httpDelete = new HttpDeleteWithBody(url);
            if (params != null) {
                httpDelete.setEntity(new StringEntity(params instanceof String ? params.toString() : GSON.toJson(params), CHARSET));
            }
            headers = headers == null ? new HashMap<String, String>() : headers;

            headers.put("Content-Type", "application/json");
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpDelete.setHeader(entry.getKey(), entry.getValue());
            }
            response = httpClient.execute(httpDelete);
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity, CHARSET);

            T t = null;
            if (type ==null || type == String.class || type == Object.class || type == void.class  || type==Void.class) {
                t = (T)result;
            }else {
                t = GSON.fromJson(result, type);
            }
            return Pair.of(response.getStatusLine().getStatusCode(), t);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (response instanceof Closeable) {
                try {
                    ((Closeable) response).close();
                } catch (IOException e) {
                }
            }
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
