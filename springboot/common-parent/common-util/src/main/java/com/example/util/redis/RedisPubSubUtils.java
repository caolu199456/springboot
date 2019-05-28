package com.example.util.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

/**
 * 订阅发布帮助类
 *
 * 订阅
 */
public class RedisPubSubUtils {
    /**
     * 订阅相关代码
     */
    /*@Bean
    RedisMessageListenerContainer container() {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(RedisUtils.getRedisTemplate().getConnectionFactory());

        //可以添加多个 messageListener
        container.addMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message, byte[] pattern) {

            }
        }, new PatternTopic("index"));

        return container;
    }*/
    /**
     * 发布消息到
     * @param topic
     * @param value
     */
    public static void sendTopic(String topic, String value) {
        if (topic == null) {
            return;
        }
        RedisUtils.getRedisTemplate().convertAndSend(topic, value);
    }

}
