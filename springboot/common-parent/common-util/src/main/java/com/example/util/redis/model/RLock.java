package com.example.util.redis.model;

import com.example.util.redis.RedisUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用于加锁
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RLock {
    private boolean getLock = false;
    private String lockKey;
    private String requestId;

    public void unlock() {
        if (isGetLock()) {
            RedisUtils.unlock(lockKey, requestId);
        }
    }
}
