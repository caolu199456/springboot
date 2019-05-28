package com.example.util.redis.callback;

import com.example.util.redis.model.DelayMsg;

/**
 * 消息到达延迟的时间进行回调
 */
public interface DelayMsgCallback {
    /**
     * 回调消息 如果有此方法抛出异常那么等待一段时间后会重新回调
     * @param delayMsg 回调的内容
     */
    void callback(DelayMsg delayMsg);
}
