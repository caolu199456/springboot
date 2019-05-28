package com.example.util.spring;

import com.alibaba.dubbo.config.spring.ReferenceBean;
import com.alibaba.dubbo.config.spring.beans.factory.annotation.ReferenceAnnotationBeanPostProcessor;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SpringUtils {

    public static ApplicationContext applicationContext;

    public static <T> T getBean(Class<T> tClass) {
        try {
            return applicationContext.getBean(tClass);
        } catch (NoSuchBeanDefinitionException e) {

        }
        return null;
    }
    public static Object getBean(String beanName) {
        try {
            return applicationContext.getBean(beanName);
        } catch (NoSuchBeanDefinitionException e) {
        }
        return null;
    }

    /**
     * 是否是开发环境
     *
     * @return
     */
    public static boolean isDev() {
        Environment environment = applicationContext.getEnvironment();
        return "dev".equalsIgnoreCase(environment.getActiveProfiles()[0]);
    }

    /**
     * 获取yml里边的值
     *
     * @param key
     * @return
     */
    public static String getConfigValue(String key) {
        Environment environment = applicationContext.getEnvironment();
        return environment.getProperty(key);
    }

    /**
     * 主动向Spring容器中注册bean
     * @param name  BeanName 传入bean的名称   为空采取默认名字 如com.example.util.SpringUtils==>springUtils
     * @param obj  对象
     * @return 返回注册到容器中的bean对象
     */
    public static synchronized void registerBean(String name, Object obj) {
        if (obj == null) {
            return;
        }
        Object bean = getBean(obj.getClass());
        if (bean != null) {
            throw new RuntimeException("对象已经存在");
        }
        if (StringUtils.isEmpty(name)) {
            String simpleName= obj.getClass().getSimpleName();
            name = (simpleName.charAt(0) + "").toLowerCase() + (simpleName.length() > 1 ? simpleName.substring(1) : "");
        }
        ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext)applicationContext;
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) configurableApplicationContext.getBeanFactory();
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(obj.getClass());
        defaultListableBeanFactory.registerSingleton(name, beanDefinitionBuilder.getRawBeanDefinition());
    }
    /**
     * 删除bean
     * @param beanName  bean的名称
     * @return 返回注册到容器中的bean对象
     */
    public static synchronized void removeBean(String beanName) {
        if (beanName == null) {
            return;
        }
        Object bean = getBean(beanName);
        if (bean != null) {
            ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) applicationContext;
            DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) configurableApplicationContext.getBeanFactory();
            defaultListableBeanFactory.removeBeanDefinition(beanName);
        }
    }

    /**
     * 缓存dubbo reference的bean
     */
    private static final Map<Class, Object> REFERENCE_BEAN_MAP = new ConcurrentHashMap<>();
    /**
     * 获取dubbo管理的bean 不能用于静态变量（需要等待spring上线问初始化后才可以使用，建议用在方法里边动态的获取）
     * @param tClass
     * @param <T>
     * @return
     */
    public static  <T> T getDubboReferenceBean(Class<T> tClass) {
        if (REFERENCE_BEAN_MAP.containsKey(tClass)) {
            return (T) REFERENCE_BEAN_MAP.get(tClass);
        }
        //采取该方式需要引入dubbo的jar包
        Collection<ReferenceBean<?>> referenceBeans = applicationContext.getBean(ReferenceAnnotationBeanPostProcessor.class).getReferenceBeans();
        if (referenceBeans!=null) {
            for (ReferenceBean<?> referenceBean : referenceBeans) {
                try {
                    if (referenceBean.getObjectType() == tClass) {
                        REFERENCE_BEAN_MAP.put(referenceBean.getObjectType(), referenceBean.getObject());
                        return (T) referenceBean.getObject();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
