package com.example.util.redis;

import com.example.util.redis.model.RLock;
import com.example.util.redis.callback.TakeLeaderCallback;
import com.example.util.security.UUIDUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.types.Expiration;

import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**aa
 * StringRedisTemplate帮助类的再次封装可以通过springboot注入 然后就可以使用
 *
 * @Configuration public class RedisConfig {
 * @Autowired private StringRedisTemplate redisTemplate;
 * @PostConstruct public void init() {
 * StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
 * RedisUtils.config(redisTemplate);
 * }
 * }
 * <p>
 * <p>
 * 本类value都采用string实现
 * 使用时请注意  如果携带有效期参数的方法都实现原子性可放心使用<br>
 * 如果没有携带有效期 请慎用
 */
public class RedisUtils {

    private static StringRedisTemplate redisTemplate;


    public RedisUtils(StringRedisTemplate redisTemplate) {
        config(redisTemplate);
    }

    /**
     * 容器启动时设置一次即可
     *
     * @param redisTemplate
     */
    public static void config(StringRedisTemplate redisTemplate) {
        RedisUtils.redisTemplate = redisTemplate;
    }


    public static StringRedisTemplate getRedisTemplate() {
        return redisTemplate;
    }

    /**
     * 队列左边添加一个值
     *
     * @param key
     * @param values
     */
    public static void leftPush(String key, String... values) {
        if (key == null || values == null) {
            return;
        }
        for (String value : values) {
            redisTemplate.opsForList().leftPush(key, value);
        }
    }

    /**
     * 取出队列所有的值
     *
     * @param key
     * @return
     */
    public static List<String> rightPop(String key) {
        if (key == null) {
            return null;
        }
        List<String> resultList = new ArrayList<>();
        ListOperations<String, String> listOperations = redisTemplate.opsForList();
        Long size = listOperations.size(key);
        for (Long i = 0L; i < size; i++) {
            resultList.add(listOperations.rightPop(key));
        }
        return resultList;
    }

    /**
     * ZADD函数
     *
     * @param key
     * @param value 如果一样会覆盖
     * @param score 分数
     */
    public static void zAdd(String key, String value, double score) {
        redisTemplate.opsForZSet().add(key, value, score);
    }

    /**
     * ZRANGE函数
     *
     * @param key
     * @param min
     * @param max
     */
    public static Set<String> zRange(String key, double min, double max) {
        return redisTemplate.opsForZSet().rangeByScore(key, min, max);
    }

    /**
     * ZDEL函数
     *
     * @param key
     * @param values
     * @return 返回的是真正删除value的个数 如果被删除的元素都不存在返回0
     */
    public static Long zDel(String key, String... values) {
        return redisTemplate.opsForZSet().remove(key, values);
    }

    /**
     * 设置一个key value
     * @param key
     * @param value
     * @return 返回上次key对应的value如果是第一次存入返回null
     */
    public static String getSet(String key, String value) {
        if (key == null || value == null) {
            return null;
        }
        return redisTemplate.opsForValue().getAndSet(key, value);
    }
    /**
     * 设置一个key value
     *
     * @param key
     * @param value
     */
    public static void set(String key, String value) {
        if (key == null || value == null) {
            return;
        }
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 设置一个key携带有效期
     *
     * @param key
     * @param value
     * @param expirationTime
     * @param timeUnit
     */
    public static void set(String key, String value, long expirationTime, TimeUnit timeUnit) {
        if (key == null || value == null) {
            return;
        }
        redisTemplate.opsForValue().set(key, value, expirationTime, timeUnit);

    }

    /**
     * 得到一个value
     *
     * @param key
     * @return
     */
    public static String get(String key) {
        if (key == null) {
            return null;
        }
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * key s1 zhang
     * key s2 wang
     * key s3 li
     *
     * @param key
     * @param hashKey
     * @param value
     */
    public static void hSet(String key, String hashKey, String value) {
        if (key == null) {
            return;
        }
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    /**
     * 通过key和hashKey得到一个值
     *
     * @param key
     * @param hashKey
     * @return
     */
    public static String hGet(String key, String hashKey) {
        if (key == null || hashKey == null) {
            return null;
        }
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        return opsForHash.get(key, hashKey);
    }

    /**
     * 得到hset所有的hashKey 和 value
     *
     * @param key
     * @return key代表hashKey
     */
    public static Map<String, String> hGetAll(String key) {
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        return opsForHash.entries(key);
    }

    /**
     * 删除hashKey对应的值
     *
     * @param key
     * @param hashKeys
     */
    public static void hDel(String key, String... hashKeys) {
        if (key == null || hashKeys == null || hashKeys.length <= 0) {

            return;
        }
        redisTemplate.opsForHash().delete(key, hashKeys);
    }

    /**
     * 判断一个key是否存在
     *
     * @param key
     * @return
     */
    public static Boolean exists(String key) {
        if (key == null) {
            return false;
        }
        return redisTemplate.hasKey(key);
    }

    /**
     * 删除指定的key
     *
     * @param key
     */
    public static void del(String key) {
        if (key != null) {
            redisTemplate.delete(key);
        }
    }

    /**
     * 模糊删除（慎用）
     * 模糊删除 a_* 就代表删除已a_开头的key *a_*删除包含a_的键
     *
     * @param key
     */
    public static void delFuzzy(String key) {
        Set<String> keys = redisTemplate.keys(key);
        if (keys != null && keys.size() > 0) {
            redisTemplate.delete(keys);
        }
    }

    /**
     * 给key设置有效期
     *
     * @param key
     * @param expirationTime
     * @param timeUnit
     */
    public static void expire(String key, long expirationTime, TimeUnit timeUnit) {
        redisTemplate.expire(key, expirationTime, timeUnit);
    }

    /**
     * 得到key剩余的过期时间
     *
     * @param key
     * @param timeUnit
     * @return -2代表key不存在 -1代表key存在但是没有设置有效期
     */
    public static Long ttl(String key, TimeUnit timeUnit) {
        return redisTemplate.getExpire(key, timeUnit);
    }

    /**
     * 自增一个key 默认从0开始自增
     *
     * @param key
     * @param increment 每次自增多少
     * @return 自增后的值
     */
    public static long incr(String key, long increment) {
        return redisTemplate.opsForValue().increment(key, increment);
    }

    /**
     * 自增设置有效期 已经实现原子性  <br>
     * 对于double的值尽量转为long然后自增 因为redis浮点型自增精度会丢失
     *
     * @param key
     * @param increment
     * @param expirationTime
     * @param timeUnit
     * @return
     */
    public static long incr(String key, long increment, long expirationTime, TimeUnit timeUnit) {
        String script =
                "local current = redis.call('incrby',KEYS[1],ARGV[1]); " +
                        "local t = redis.call('ttl',KEYS[1]); " +
                        "if t ~= -2 then  " +
                        "redis.call('expire',KEYS[1],ARGV[2]) " +
                        "end; " +
                        "return current";
        return redisTemplate.execute(new RedisCallback<Long>() {

            @Override
            public Long doInRedis(RedisConnection redisConnection) throws DataAccessException {
                return redisConnection.eval(
                        script.getBytes(),
                        ReturnType.fromJavaType(Long.class),
                        1,
                        key.getBytes(),
                        (increment + "").getBytes(),
                        (timeUnit.toSeconds(expirationTime) + "").getBytes()
                );
            }
        });
    }

    /**
     * 循环自增设置有效期 已经实现原子性  <br>
     * 对于double的值尽量转为long然后自增 因为redis浮点型自增精度会丢失
     *
     * @param key
     * @param increment      支持负数
     * @param min
     * @param max
     * @param expirationTime
     * @param timeUnit
     * @return
     */
    public static long loopIncr(String key, long increment, long min, long max, long expirationTime, TimeUnit timeUnit) {

        //KEYS[1]代表key
        //ARGV[1]代表increment
        //ARGV[2]代表min
        //ARGV[3]代表max
        //ARGV[4]代表expirationTime
        String script =
                "local current = tonumber(redis.call('incrby',KEYS[1],ARGV[1])); " +
                        //如果大于最大值则变为最小值
                        "if (current>tonumber(ARGV[3])) then " +
                        "redis.call('set',KEYS[1],ARGV[2]) " +
                        "current =  tonumber(ARGV[2]) " +
                        "end;" +
                        //如果最小则变为最大
                        "if (current< tonumber(ARGV[2])) then " +
                        "redis.call('set',KEYS[1],ARGV[3]) " +
                        "current = tonumber(ARGV[3]) " +
                        "end; " +
                        "redis.call('expire',KEYS[1],ARGV[4]) " +
                        "return current";
        return redisTemplate.execute(new RedisCallback<Long>() {

            @Override
            public Long doInRedis(RedisConnection redisConnection) throws DataAccessException {
                return redisConnection.eval(
                        script.getBytes(),
                        ReturnType.fromJavaType(Long.class),
                        1,
                        key.getBytes(),
                        (increment + "").getBytes(),
                        (min + "").getBytes(),
                        (max + "").getBytes(),
                        (timeUnit.toSeconds(expirationTime) + "").getBytes()
                );
            }
        });
    }

    /**
     * @param key
     * @param hashKey
     * @return
     */
    public static long hIncr(String key, String hashKey) {
        return hIncr(key, hashKey, 1);
    }

    /**
     * @param key
     * @param hashKey
     * @param increment
     * @return
     */
    public static long hIncr(String key, String hashKey, long increment) {
        return redisTemplate.opsForHash().increment(key, hashKey, increment);
    }

    /**
     * 得到redis时间 集群环境获取返回集群里边随机机器的时间
     *
     * @return
     */
    public static Long getRedisCurrentMillis() {
        return redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.time();
            }
        });
    }

    /**
     * setnx函数 key存在返回false 不设置有效期慎用
     * set name 1 px 100000 nx
     *
     * @param key
     * @param value
     * @return
     */
    public static boolean setnx(String key, String value) {

        return redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                Boolean result = connection.setNX(key.getBytes(), value.getBytes());
                return result;
            }
        });
    }

    /**
     * setnx函数 key存在返回false key不存在返回true
     * set name 1 px 100000 nx
     *
     * @param key
     * @param value
     * @param expirationTime
     * @param timeUnit
     * @return
     */
    public static boolean setnx(String key, String value, long expirationTime, TimeUnit timeUnit) {

        return redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                Boolean result = connection.set(key.getBytes(), value.getBytes(), Expiration.from(expirationTime, timeUnit), RedisStringCommands.SetOption.SET_IF_ABSENT);
                return result;
            }
        });
    }


    /**
     * 加锁 一定要调用unlock方法
     *
     * @param key
     * @param expirationTime
     * @param timeUnit
     * @return
     */
    public static RLock tryLock(String key,long expirationTime, TimeUnit timeUnit) {
        String requestId = UUIDUtils.gen32UUID();
        /**
         * 产生一个RequestId
         */

        long expirationTimeAt = System.currentTimeMillis() + timeUnit.toMillis(expirationTime) + 1;
        while ( System.currentTimeMillis() < expirationTimeAt) {
            if (setnx(key, requestId, expirationTime, timeUnit)) {
                /**
                 * 获得锁
                 */
                return new RLock(true, key, requestId);
            }
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return new RLock(false, key, requestId);
    }

    /**
     * 解锁
     *
     * @param key
     * @return
     */
    public static boolean unlock(String key,String requestId) {

        return redisTemplate.execute(new RedisCallback<Boolean>() {

            @Override
            public Boolean doInRedis(RedisConnection redisConnection) throws DataAccessException {
                String script = "if (redis.call('get', KEYS[1]) == ARGV[1]) then redis.call('del', KEYS[1]) return 1 end return 0";
                Long result = redisConnection.eval(
                        script.getBytes(),
                        ReturnType.fromJavaType(Long.class),
                        1,
                        key.getBytes(), StringUtils.isBlank(requestId) ? "1".getBytes() : requestId.getBytes()
                );
                return result != null && result.intValue() == 1;
            }
        });
    }

}
