package com.example.cms.config;

import com.example.util.redis.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Configuration
public class RedisConfig {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @PostConstruct
    public void init() {
        /**
         * key策略都用string
         */
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        /**
         * value用对象形式
         */
        /*redisTemplate.setValueSerializer(new KryoSerializer());
        redisTemplate.setHashValueSerializer(new KryoSerializer());*/

        redisTemplate.setValueSerializer(stringRedisSerializer);
        redisTemplate.setHashValueSerializer(stringRedisSerializer);
        RedisUtils.config(redisTemplate);
    }

    /**
     * 容器销毁前清除注册记录
     */
    @PreDestroy
    public void destroy() {
        System.out.println("111");
    }


    /*private static class KryoSerializer implements RedisSerializer {

        @Override
        public byte[] serialize(Object t) throws SerializationException {
            return KryoKit.fromObject(t);
        }

        @Override
        public Object deserialize(byte[] bytes) throws SerializationException {
            return KryoKit.toObject(bytes);
        }
    }*/
}
