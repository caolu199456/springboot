package com.example.util.pool.string;

import org.apache.commons.pool2.impl.GenericObjectPool;

public class StringBufferTest {
    public static void main(String[] args) throws Exception {
        GenericObjectPool<StringBuffer> pool = new GenericObjectPool<>(new StringBufferFactory());
        for (int i = 0; i < 10; i++) {
            System.out.println("存活对象2："+pool.getNumActive());
            StringBuffer sb = pool.borrowObject();

            for (int i1 = 0; i1 < 10; i1++) {
                sb.append("a");
            }

            System.out.println(sb.toString());

            pool.returnObject(sb);
            System.out.println("存活对象3："+pool.getNumActive());

        }
    }
}
