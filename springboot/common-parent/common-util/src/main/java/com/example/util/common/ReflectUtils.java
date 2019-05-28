package com.example.util.common;

import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 反射帮助类
 */
public class ReflectUtils {

    /**
     * 反射某个字段的值
     *
     * @param obj 反射的对象
     * @param methodName 方法名称
     * @param args 犯法参数
     * @return
     */
    public static Object invokeMethod(Object obj, String methodName, Object... args) {

        Method[] declaredMethods = obj.getClass().getDeclaredMethods();
        if (declaredMethods != null) {
            for (Method declaredMethod : declaredMethods) {
                declaredMethod.setAccessible(true);
                if (declaredMethod.getName().equals(methodName)) {
                    try {
                        Object invoke = declaredMethod.invoke(obj, args);
                        return invoke;
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }

    /**
     * 反射某个字段的值
     * @param obj 反射的对象
     * @param fieldName 字段名称
     * @return
     */
    public static Object getFieldValue(Object obj, String fieldName) {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            if (field != null) {
                field.setAccessible(true);
                return field.get(obj);
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 反射子类所有的属性
     * @param clazz 反射的类
     * @param containsSuperclassField 是否包含父类的字段
     * @return
     */
    public static List<String> getField(Class clazz, boolean containsSuperclassField) {
        List<String> fieldList = new ArrayList<>();
        Field[] declaredFields = clazz.getDeclaredFields();
        if (declaredFields!=null) {
            for (Field declaredField : declaredFields) {
                declaredField.setAccessible(true);
                fieldList.add(declaredField.getName());
            }
        }
        if (containsSuperclassField) {
            Class superclass = clazz.getSuperclass();
            if (superclass != null && superclass != Object.class) {
                fieldList.addAll(getField(superclass, true));
            }
        }
        return fieldList;
    }
    /**
     * 设置对象的值
     * @param obj 要设置的类
     * @param params <字段名称,字段值>
     * @return
     */
    public static void setFields(Object obj, Map<String,Object> params) {
        if (obj == null || params == null || params.isEmpty()) {
            return;
        }
        Field[] declaredFields = obj.getClass().getDeclaredFields();
        if (declaredFields!=null) {
            for (Field declaredField : declaredFields) {
                declaredField.setAccessible(true);
                Class<?> type = declaredField.getType();

                JSONObject jsonObject = new JSONObject(params);
                Object object = jsonObject.getObject(declaredField.getName(), type);

                if (object != null) {
                    try {
                        declaredField.set(obj,object);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
