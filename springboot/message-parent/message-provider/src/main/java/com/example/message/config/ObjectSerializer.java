package com.example.message.config;

import com.example.util.common.KryoKit;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class ObjectSerializer implements Serializer<Object> {
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public byte[] serialize(String topic, Object data) {
        if (data == null) {
            return null;
        }
        return KryoKit.fromObject(data);
    }

    @Override
    public void close() {

    }
}
