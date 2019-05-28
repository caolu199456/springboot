package com.example.util.redis.model;

import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 延迟消息
 */
@Data
public class DelayMsg {
    /**
     * 消息分类 到消息达到延迟条件的时候会向该topic推送消息
     */
    private String topic;
    /**
     * 消息id 由使用者指定 用户取消
     */
    private String msgId;
    /**
     * 消息产生时间 默认当前时间
     */
    private Date createAt=new Date();
    /**
     * 延迟多少毫秒
     */
    private long delayMillis;
    /**
     * time to live 消息存货时间(毫秒) 如果消息投递失败则会重试 直到消息过期为止
     */
    private long ttl;
    /**
     * 业务参数
     */
    private String bizContent;

    public DelayMsg() {
    }

    public DelayMsg(String topic, String msgId, long delayMillis, long ttl) {
        this(topic, msgId, delayMillis, ttl, null);
    }

    public DelayMsg(String topic, String msgId, long delayMillis, long ttl,String bizContent) {
        if (topic == null) {
            throw new RuntimeException("topic必须设定");
        }
        if (msgId == null) {
            throw new RuntimeException("msgId必须设定");
        }
        if (delayMillis <= 0) {
            throw new RuntimeException("延迟时间必须设定");
        }
        if (ttl <= 0) {
            throw new RuntimeException("必须设定消息存活时间");
        }
        this.topic = topic;
        this.msgId = msgId;
        this.delayMillis = delayMillis;
        this.ttl = ttl;
        this.bizContent = bizContent;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return "DelayMsg{" +
                "topic='" + topic + '\'' +
                ", 消息ID='" + msgId + '\'' +
                ", 创建消息的时间=" + sdf.format(createAt) +
                ", 延迟毫秒数=" + delayMillis +
                ", 应该过期的时间=" + sdf.format(new Date(createAt.getTime()+delayMillis)) +
                ", 现在时间=" + sdf.format(new Date()) +
                ", 消息过期时的毫秒数=" + ttl +
                ", 业务参数='" + bizContent + '\'' +
                '}';
    }
}
