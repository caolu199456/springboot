package com.example.util.redis;

import com.alibaba.fastjson.JSON;
import com.example.util.redis.callback.DelayMsgCallback;
import com.example.util.redis.model.DelayMsg;
import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 利用redis实现延迟队列
 */
public class DelayQueueUtils {
    /**
     * 每一个topic扫描线程数量 必须为2的N次方 默认8个  禁止上线后调整
     */
    private static final int SCAN_THREAD_NUM = 1 << 3;
    /**
     * 每个topic的线程组
     */
    private static final Map<String, List<ScheduledExecutorService>> TOPIC_SCHEDULE_MAP = new ConcurrentHashMap<>();
    /**
     * 防止并发
     */
    private static final Object LISTEN_LOCK = new Object();


    /**
     * 延迟消息信息
     *
     * @param delayMsg
     * @return
     */
    public static boolean putDelayMsg(DelayMsg delayMsg) {
        try {
            //存储消息 topic名称+消息id
            RedisUtils.set(delayMsg.getTopic() + "_" + delayMsg.getMsgId(), JSON.toJSONString(delayMsg), delayMsg.getTtl(), TimeUnit.MILLISECONDS);
            //存储延迟队列
            RedisUtils.zAdd(getRealTopic(delayMsg.getTopic(),delayMsg.getMsgId()), delayMsg.getMsgId(), delayMsg.getCreateAt().getTime() + delayMsg.getDelayMillis());
            return true;
        } catch (Exception e) {
        }
        return false;
    }
    /**
     * 取消延迟消息
     * @param topic
     * @param msgId
     * @return
     */
    public static boolean cancelDelayMsg(String topic,String msgId) {
        try {
            //存储消息
            RedisUtils.del(topic + "_" + msgId);
            //存储延迟队列
            RedisUtils.zDel(getRealTopic(topic, msgId), msgId);
            return true;
        } catch (Exception e) {
        }
        return false;
    }
    /**
     * 监听达到延迟条件的消息
     *
     * @param topic
     * @return
     */
    public static void listenDelayMsg(String topic, DelayMsgCallback callback) {
        synchronized (LISTEN_LOCK) {
            List<ScheduledExecutorService> executorServiceGroup = TOPIC_SCHEDULE_MAP.get(topic);
            if (executorServiceGroup==null) {
                executorServiceGroup = new ArrayList<>(SCAN_THREAD_NUM);
                for (int i = 0; i < SCAN_THREAD_NUM; i++) {

                    ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1);
                    //要扫描的topic
                    String topic_i = topic + "_" + i;
                    executorService.scheduleAtFixedRate(() -> {
                        try {
                            //开始取数据
                            Set<String> msgIdSet = RedisUtils.zRange(topic_i, 0, System.currentTimeMillis());
                            if (msgIdSet==null) {
                                return;
                            }
                            for (String msgId : msgIdSet) {
                                String delayMsgJson = RedisUtils.get(topic + "_" + msgId);
                                if (StringUtils.isNotBlank(delayMsgJson)) {
                                    DelayMsg delayMsg = JSON.parseObject(delayMsgJson, DelayMsg.class);
                                    try {
                                        Long delCount = RedisUtils.zDel(topic_i, msgId);
                                        if (delCount != null && delCount > 0) {
                                            //说明当前线程拿到了这个延迟队列

                                            //删除消息内容
                                            RedisUtils.del(topic + "_" + msgId);

                                            callback.callback(delayMsg);
                                        }
                                    } catch (Exception e) {
                                        //说明业务系统处理出错那么将重新存入
                                        putDelayMsg(delayMsg);
                                    }
                                }else {
                                    //消息过期直接删除该任务
                                    RedisUtils.zDel(topic_i, msgId);
                                }
                            }
                        } catch (Exception e) {
                            //防止抛出异常线程池不在运行
                            e.printStackTrace();
                        }
                    }, 0, 10, TimeUnit.SECONDS);
                    executorServiceGroup.add(executorService);
                }
            }
            //把当前线程组存入
            TOPIC_SCHEDULE_MAP.put(topic, executorServiceGroup);
        }
    }

    /**
     * 传入topic名称得到路由后的topic
     * @param topic 要监听的topic
     * @param msgId 消息id用于路由
     * @return
     */
    private static String getRealTopic(String topic,String msgId) {
        return topic + "_" + (hash(msgId) & (SCAN_THREAD_NUM - 1));
    }
    private static int hash(String msgId) {
        return Objects.hashCode(msgId);
    }
}
