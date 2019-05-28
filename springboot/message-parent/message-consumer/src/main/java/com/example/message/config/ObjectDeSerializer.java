package com.example.message.config;

import com.example.util.common.KryoKit;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

public class ObjectDeSerializer implements Deserializer<Object> {
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public Object deserialize(String topic, byte[] data) {
        return KryoKit.toObject(data);
    }

    @Override
    public void close() {

    }
}
