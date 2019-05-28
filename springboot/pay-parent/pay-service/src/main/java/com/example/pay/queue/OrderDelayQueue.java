/*
package com.example.pay.queue;

import com.alibaba.fastjson.JSON;
import com.example.util.kit.OrderUtils;
import com.example.util.redis.DelayQueueUtils;
import com.example.util.redis.RedisPubSubUtils;
import com.example.util.redis.callback.DelayMsgCallback;
import com.example.util.redis.model.DelayMsg;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class OrderDelayQueue {
    public OrderDelayQueue() {
       DelayQueueUtils.listenDelayMsg("abcdef", new DelayMsgCallback() {
           @Override
           public void callback(DelayMsg delayMsg) {
               System.err.println(JSON.toJSONString(delayMsg));
           }
       });
        new Thread(){
            @Override
            public void run() {
                mock();
            }
        }.start();
    }

    private void mock() {
        while (true) {
            DelayQueueUtils.putDelayMsg(
                    new DelayMsg("abcdef", OrderUtils.getOrderNo(), 20000L, 10000000L, null));
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
*/
