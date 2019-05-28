package com.example.util.common;

import com.alibaba.fastjson.JSON;
import com.example.util.security.MD5;
import com.google.gson.Gson;
import lombok.Getter;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * map的工具类不提供传输使用
 */
public class MapUtils {

    private static final Gson GSON = new Gson();

    @Getter
    private final Map<String, Object> initMap;


    public MapUtils() {
        this.initMap = new LinkedHashMap<>();
    }

    public MapUtils(Map<String, Object> initMap) {
        this.initMap = initMap;
    }

    public Boolean isNotNullAndNotEmpty(String... keys) {
        if (keys == null) {
            return false;
        }
        for (String key : keys) {
            if (!this.initMap.containsKey(key)) {
                return false;
            }
            if (this.initMap.get(key) == null) {
                return false;
            }
            if (this.initMap.get(key).toString().trim().length() == 0) {
                return false;
            }
        }
        return true;
    }

    public Boolean isNullOrEmpty(String... keys) {
        return !isNotNullAndNotEmpty(keys);
    }



    public <T> T  convertObject(Class<T> clazz) {
        return GSON.fromJson(GSON.toJson(this.initMap), clazz);
    }


    /**
     * 根据key得到value
     * @param keys
     * @return
     */
    public Object[] getValues(String... keys) {
        if (keys == null) {
            return null;
        }
        Object[] values =new Object[keys.length];
        for (int i = 0; i < keys.length; i++) {
            values[i] = this.initMap.get(keys);
        }

        return values;


    }


    public String genUUID(){

        return UUID.randomUUID().toString().replaceAll("-","");
    }

    public void like(String key) {
        if (this.initMap.containsKey(key)) {
            this.initMap.put(key,"%"+this.initMap.get(key).toString()+"%");
        }
    }

    public String MD5(String key) {
        if (this.initMap.containsKey(key)) {
            return MD5.md5(this.initMap.get(key).toString());
        }
        return null;
    }

    public String getString(String key) {
        if (!this.initMap.containsKey(key)) {
            return null;
        }
        if (this.initMap.get(key) == null) {
            return null;
        }
        return this.initMap.get(key).toString();

    }
    public String[] getValues(String key) {
        if (!this.initMap.containsKey(key)) {
            return null;
        }
        if (this.initMap.get(key) == null) {
            return null;
        }
        return (String[]) this.initMap.get(key);

    }
    public Short getShort(String key) {
        if (!this.initMap.containsKey(key)) {
            return null;
        }
        if (this.initMap.get(key) == null) {
            return null;
        }
        return Short.parseShort(this.getString(key));
    }
    public Integer getInt(String key) {
        if (!this.initMap.containsKey(key)) {
            return null;
        }
        if (this.initMap.get(key) == null) {
            return null;
        }
        return Integer.parseInt(this.getString(key));
    }
    public Long getLong(String key) {
        if (!this.initMap.containsKey(key)) {
            return null;
        }
        if (this.initMap.get(key) == null) {
            return null;
        }
        return Long.parseLong(this.getString(key));
    }
    public Double getDouble(String key) {
        if (!this.initMap.containsKey(key)) {
            return null;
        }
        if (this.initMap.get(key) == null) {
            return null;
        }
        return Double.parseDouble(this.getString(key));
    }
    public Float getFloat(String key) {
        if (!this.initMap.containsKey(key)) {
            return null;
        }
        if (this.initMap.get(key) == null) {
            return null;
        }
        return Float.parseFloat(this.getString(key));
    }
    public BigDecimal getBigDecimal(String key) {
        if (!this.initMap.containsKey(key)) {
            return null;
        }
        if (this.initMap.get(key) == null) {
            return null;
        }
        return new BigDecimal(this.getString(key));
    }
    /**
     * 精确计算  四舍五入
     * number 小数点 几位
     * targetObj 目标对象
     */
    public BigDecimal getBigDecimal(String key,Integer number){
        if (!this.initMap.containsKey(key)) {
            return null;
        }
        if (this.initMap.get(key) == null) {
            return null;
        }
        if(number == null || number < 0){
            number = 2;
        }
        return getBigDecimal(key).setScale(number, BigDecimal.ROUND_HALF_UP); //四舍五入
    }

    public Boolean getBoolean(String key) {
        if (!this.initMap.containsKey(key)) {
            return false;
        }
        if (this.initMap.get(key) == null) {
            return false;
        }
        return Boolean.parseBoolean(this.getString(key));
    }
    /**
     * 得到日期
     * @param key
     * @param format 可以传null("yyyy-MM-dd HH:mm:ss")  默认为yyyy-MM-dd
     * @return
     * @throws ParseException
     */
    public Date getDate(String key, String format) throws ParseException {
        if (!this.initMap.containsKey(key)) {
            return null;
        }
        if (this.initMap.get(key) == null) {
            return null;
        }
        if (format == null) {
            format = new String("yyyy-MM-dd");
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = sdf.parse(this.initMap.get(key).toString());
        return date;
    }
    /**
     * 得到日期字符串
     * @param format 可以传null("yyyy-MM-dd HH:mm:ss")  默认为yyyy-MM-dd HH:mm:ss
     * @return
     * @throws ParseException
     */
    public static String getCurrentDate(String format) {
        if (format == null) {
            format = new String("yyyy-MM-dd HH:mm:ss");
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date());
    }

    public static void main(String[] args) {
        Map map = new HashMap();
        map.put("1", "caolu");
        map.put("date", new Date());
        map.put(12, "1");

        MapUtils mapUtils = new MapUtils(map);

        String toJSONString = JSON.toJSONString(mapUtils);
        System.out.println(toJSONString);

        System.out.println(GSON.toJson(GSON.fromJson(toJSONString,MapUtils.class)));

    }
    
}
