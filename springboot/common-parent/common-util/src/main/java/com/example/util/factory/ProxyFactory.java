package com.example.util.factory;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ProxyFactory  implements InvocationHandler{


    private Object proxyObj;


    public ProxyFactory(Object proxyObj) {
        this.proxyObj = proxyObj;
    }

    /**
     * 得到代理对象
     * @param clazz 代理的类型
     * @param <T>
     * @return
     */
    public <T> T getProxyInstance(Class<T> clazz) {

        return (T)Proxy.newProxyInstance(this.proxyObj.getClass().getClassLoader(),
                this.proxyObj.getClass().getInterfaces(), this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        return method.invoke(this.proxyObj,args);
    }
}
