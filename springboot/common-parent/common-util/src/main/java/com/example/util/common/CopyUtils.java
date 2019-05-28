package com.example.util.common;

import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

public class CopyUtils {
    /**
     * 拷贝list
     * @param fromList 来源
     * @param clazz 拷贝后list的泛型
     * @param <T>
     * @return
     */
    public static <T> List<T> copyList(List fromList,Class<T> clazz){

        List<T> toList = new ArrayList<>();

        if (fromList != null && fromList.size() > 0) {
            for (Object fromObj : fromList) {
                try {
                    T t = clazz.newInstance();
                    BeanUtils.copyProperties(fromObj,t);
                    toList.add(t);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

            }
        }
        return toList;
    }
    /**
     * 拷贝一个对象
     * @param fromObj 来源
     * @param clazz 拷贝后list的泛型
     * @param <T>
     * @return
     */
    public static <T> T copyObj(Object fromObj,Class<T> clazz){

        if (fromObj != null) {
                try {
                T t = clazz.newInstance();
                BeanUtils.copyProperties(fromObj,t);
                return t;
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
