package com.example.redis;

import com.example.BaseTest;
import com.example.util.redis.RedisUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.RedisSerializer;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class RedisTest extends BaseTest {
}
