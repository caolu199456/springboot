package com.example.util.redis;

import com.example.util.redis.callback.TakeLeaderCallback;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 选举帮助类
 */
public class TakeLeaderUtils {


    /**
     * 心跳发送的定时任务
     */
    private static final ScheduledExecutorService EXECUTOR_SERVICE = new ScheduledThreadPoolExecutor(1,
            new BasicThreadFactory.Builder().namingPattern("take-leader-schedule-pool-%d").daemon(true).build());

    /**
     * @param name     业务名称
     * @param callback 获取锁之后的操作
     */
    public static void taskLeader(
            String name,
            TakeLeaderCallback callback
    ) {
        //过期时间
        int expireTime = 60;
        try {
            boolean isLeader = false;
            String lockValue = UUID.randomUUID().toString();
            boolean isSuccess = RedisUtils.setnx(name, lockValue, expireTime, TimeUnit.SECONDS);
            if (isSuccess) {
                //说明是leader
                callback.onTakeLeader();
                isLeader = true;
            } else {
                //说明是flower
                callback.onTakeFollower();
            }
            /**
             * 5次不发送心跳重新选举
             */
            boolean finalIsLeader = isLeader;
            EXECUTOR_SERVICE.scheduleAtFixedRate(() -> {
                try {
                    String value = RedisUtils.get(name);
                    if (value == null) {
                        if (finalIsLeader) {
                            //我本身就是leader 我还在活着那我还是leader(只要不重启我就是leader 防止因为心跳未发送重新选举)
                            RedisUtils.set(name, lockValue, expireTime, TimeUnit.SECONDS);
                        }else{
                            //重新选举
                            boolean result = RedisUtils.setnx(name, lockValue, expireTime, TimeUnit.SECONDS);
                            if (result) {
                                callback.onTakeLeader();
                            }
                        }
                    } else {
                        if (Objects.equals(lockValue, value)) {
                            //说明是本机获取的leader则只需刷新内存
                            RedisUtils.expire(name, expireTime, TimeUnit.SECONDS);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, 0, expireTime / 5, TimeUnit.SECONDS);

        } catch (Exception e) {
            callback.onFailed(e);
        }
    }



    public static void main(String[] args) {
        TakeLeaderUtils.taskLeader("LUDENG_LIXIAN_GAOJING_TASK", new TakeLeaderCallback() {
            @Override
            public void onTakeLeader() {

            }
        });
    }

}
