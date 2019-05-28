package com.example.util.common;

import java.util.Collection;
import java.util.Map;

/**
 * 集合帮助类
 */
public class CollectionUtils {
    /**
     * 判断集合是否为空
     * @param collection
     * @return
     */
    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }
    /**
     * 判断集合是否为空
     * @param collection
     * @return
     */
    public static boolean isEmpty(Map collection) {
        return collection == null || collection.isEmpty();
    }
    /**
     * 判断集合是否不为空
     * @param collection
     * @return
     */
    public static boolean isNotEmpty(Collection collection) {
        return !isEmpty(collection);
    }
}
