package com.example.util.common;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;

import java.util.HashMap;
import java.util.Map;

public class KryoKit {
    private static KryoFactory factory = new KryoFactory() {
        @Override
        public Kryo create() {

            Kryo kryo = new Kryo();

            return kryo;
        }
    };

    private static KryoPool pool = new KryoPool.Builder(factory).softReferences().build();

    public static KryoPool getPool() {
        return pool;
    }

    public static <T> T toObject(byte[] bytes, Class<T> classz) {
        return (T) toObject(bytes);
    }

    public static Object toObject(byte[] bytes) {
        Kryo kryo = null;

        Input input = null;
        Object obj = null;
        try {
            kryo = pool.borrow();
            input = new Input(bytes);
            obj = kryo.readClassAndObject(input);
        } catch (KryoException e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                input.close();
            }
            if (pool != null) {
                pool.release(kryo);
            }
            return obj;
        }
    }

    public static byte[] fromObject(Object object) {
        Kryo kryo = null;

        Output output = null;
        byte[] bytes = null;

        try {
            kryo = pool.borrow();
            output = new Output(1024, -1);
            kryo.writeClassAndObject(output, object);
            bytes = output.toBytes();
        } catch (KryoException e) {
            e.printStackTrace();
        } finally {

            if (output != null) {
                output.flush();
                output.close();
            }
            if (pool != null) {
                pool.release(kryo);

            }
            return bytes; //获得byte数据,这些数据可用作储存、网络传输等...
        }
    }

    public static void main(String[] args) {
        /*for (int i = 0; i < 100; i++) {
            new Thread(){
                @Override
                public void run() {
                    Map map = new HashMap();
                    map.put("1", "2");
                    byte[] bytes = fromObject(map);
                    System.out.println(toObject(bytes,Map.class));
                    Map map1 = new HashMap();
                    map1.put("1", "2");
                    byte[] bytes1 = fromObject(map1);
                    System.out.println(toObject(bytes1,Map.class));
                }
            }.start();

        }*/
        System.out.println("--------");
        for (int i = 0; i < 3; i++) {
            Map map = new HashMap();
            map.put("1", "2");
            byte[] bytes = fromObject(map);
            System.out.println(toObject(bytes, Map.class));
            Map map1 = new HashMap();
            map1.put("1", "2");
            byte[] bytes1 = fromObject(map1);
            System.out.println(toObject(bytes1, Map.class));

        }
    }

}
