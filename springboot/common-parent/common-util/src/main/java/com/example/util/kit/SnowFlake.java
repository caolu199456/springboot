package com.example.util.kit;

import com.example.util.redis.RedisUtils;
import com.example.util.security.UUIDUtils;
import lombok.Data;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 雪花算法解析 结构 snowflake的结构如下(每部分用-分开):
 * 0 - 00000000000000000000000000000000000000000 - 0000000000 - 000000000000
 * 第一位为未使用 表示正数
 * 接下来的41位为毫秒级时间(41位的长度可以使用69年)
 * 然后是10位的长度最多支持部署1024个节点 0~1023
 *  最后12位是毫秒内的计数（12位的计数顺序号支持每个节点每毫秒产生4096个ID序号）
 * 一共加起来刚好64位，为一个Long型。
 */
public class SnowFlake {
    /**
     * 起始的时间戳 2018-01-01 00:00:00.000
     */
    private final static long START_STMP = 1514736000000L;

    /**
     * 机器标识 0~1023
     */
    private static int machineId = -1;
    /**
     * 序列号 0~4095
     */
    private static int sequence = 0;
    /**
     * 上一次时间戳
     */
    private static long lastStmp = -1L;


    private static final Object CREATE_ID_LOCK = new Object();

    private static  ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1);


    public SnowFlake(String projectName) {
        config(projectName);
    }

    /**
     * 项目名称
     * @param projectName
     */
    public static void config(String projectName){
        if (executorService.isShutdown()) {
            executorService = new ScheduledThreadPoolExecutor(1);
        }
        String randomStr = UUIDUtils.gen32UUID();
        short i = 0;
        for (i = 0; i < 1024; i++) {
            String lockId = String.format(projectName + "_CREATE_MACHINE_ID_" + i);
            //有效期60分钟
            boolean setnx = RedisUtils.setnx(lockId, randomStr, 5, TimeUnit.MINUTES);
            if (setnx) {
                SnowFlake.machineId = i;
                executorService.scheduleAtFixedRate(() -> {
                    try {
                        //有效期为5分钟 如果5分钟内没有上报心跳那么这个虚拟机器id就会被收回 我们在这里1分钟报一次
                        String saveRandomStr = RedisUtils.get(lockId);
                        if (Objects.equals(saveRandomStr, randomStr)) {
                            //如果是我存的机器号那就重新设置有效期(存在当前机器没有挂掉等待5分钟后继续上报心跳，在这个过程中别的机器可能占用了当前机器的id 这种情况不能重新设置)
                            RedisUtils.set(lockId, randomStr, 5, TimeUnit.MINUTES);
                        } else {
                            //重新获取
                            executorService.shutdown();
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            config(projectName);
                        }
                    } catch (Exception e) {
                        throw new RuntimeException("SnowFlake心跳任务失败", e);
                    }
                }, 0, 1, TimeUnit.MINUTES);
                return;
            }
        }
        if (i >= 1024) {
            throw new RuntimeException("机器id号已经用尽");
        }
    }
    /**
     * 产生下一个ID
     *
     * @return
     */
    public static long nextId() {
        if (machineId < 0) {
            throw new RuntimeException("你必须配置机器id");
        }
        synchronized (CREATE_ID_LOCK) {
            long currStmp = getNewStmp();
            if (currStmp < lastStmp) {
                if (lastStmp - currStmp > 20L * 1000) {
                    //时钟回拨20秒以上
                    throw new RuntimeException("时钟回滚超过20秒拒绝生成id");
                }else {
                    try {
                        //等待时间的二倍
                        Thread.sleep((lastStmp - currStmp) << 1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (currStmp == lastStmp) {
                //相同毫秒内，序列号自增
                sequence = (sequence + 1) & 4095;
                //同一毫秒的序列数已经达到最大
                if (sequence == 0L) {
                    currStmp = getNextMill();
                }
            } else {
                //不同毫秒内，序列号置为0  低并发情况下这个值会大多数情况为0 如果采取分片建议去0~9随机值
                sequence = 0;
            }

            lastStmp = currStmp;

            return (currStmp - START_STMP) << 22 //时间戳部分
                    | machineId << 12             //机器标识部分
                    | sequence;                    //序列号部分
        }
    }

    /**
     * id反向解析
     *
     * @return
     */
    public static IdParseInfo parse(long id) {
        IdParseInfo idParseInfo = new IdParseInfo();
        //由于时间在最高位直接右移动到底即可不需要与最大值 再加上其实时间
        idParseInfo.setCreateTime(new Date(((id >> 22)) + START_STMP));
        //右移后最高位可能有值所以在与上最大值
        idParseInfo.setMachineId((int) (id >> 12) & 1023);
        idParseInfo.setSequence((int) (id & 4095));
        return idParseInfo;
    }

    private static long getNextMill() {
        long mill = getNewStmp();
        while (mill <= lastStmp) {
            mill = getNewStmp();
        }
        return mill;
    }

    private static long getNewStmp() {
        return System.currentTimeMillis();
    }


    public static void main(String[] args) {


        long t1 = System.currentTimeMillis();

        for (int i = 0; i < 1; i++) {
            System.out.println(SnowFlake.nextId());
        }

        System.out.println(System.currentTimeMillis()-t1);

    }


    /**
     * id解析
     */
    @Data
    public static class IdParseInfo {
        /**
         * 这个id产生的时间
         */
        private Date createTime;
        /**
         * 机器id
         */
        private int machineId;
        /**
         * 序列号
         */
        private int sequence;

    }
}
