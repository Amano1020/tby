package com.tby.api.util;

import lombok.RequiredArgsConstructor;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class IdempotencyUtil {

    private final RedissonClient redissonClient;

    private static final String LOCK_PREFIX = "lock:";

    /**
     * @param token The unique request token provided by the client.
     * @return true if the lock was acquired and process can run (first request),
     *         false if it
     *         already exists or cannot acquire (duplicate).
     */
    public boolean checkAndSetToken(String token) {
        if (token == null || token.isEmpty()) {
            return true;
        }

        String lockKey = LOCK_PREFIX + token;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            return lock.tryLock(0, -1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Lock acquisition was interrupted", e);
        }
    }

    /**
     * Unlock the idemptotency token when processing is done or fails.
     */
    public void unlockToken(String token) {
        if (token == null || token.isEmpty()) {
            return;
        }

        String lockKey = LOCK_PREFIX + token;
        RLock lock = redissonClient.getLock(lockKey);
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }
}
