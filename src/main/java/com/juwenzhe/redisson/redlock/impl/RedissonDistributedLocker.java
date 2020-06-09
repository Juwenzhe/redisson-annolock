package com.juwenzhe.redisson.redlock.impl;

import com.juwenzhe.redisson.redlock.DistributedLocker;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 分布式锁操作类
 * @author juwenzhe123@163.com
 * @date 2020/6/8 20:57
 */
@Component
public class RedissonDistributedLocker implements DistributedLocker {

    public final static String LOCKER_PREFIX = "redlock:";

    @Autowired
    private RedissonClient redissonClient;

    public RLock getLock(String lockKey) {
        return redissonClient.getLock(getRlockKey(lockKey));
    }

    public RLock lock(String lockKey) {
        RLock lock = getLock(lockKey);
        lock.lock();
        return lock;
    }

    public RLock lock(String lockKey, long leaseTime) {
        RLock lock = getLock(lockKey);
        lock.lock(leaseTime, TimeUnit.SECONDS);
        return lock;
    }

    public RLock lock(String lockKey, long leaseTime, TimeUnit unit) {
        RLock lock = getLock(lockKey);
        lock.lock(leaseTime, unit);
        return lock;
    }

    public boolean tryLock(String lockKey, long waitTime, long leaseTime, TimeUnit unit) {
        RLock lock = getLock(lockKey);
        try {
            return lock.tryLock(waitTime, leaseTime, unit);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean tryLock(RLock lock, long waitTime, long leaseTime, TimeUnit unit) {
        if (lock == null) {
            return false;
        }
        try {
            return lock.tryLock(waitTime, leaseTime, unit);
        } catch (Exception e) {
            return false;
        }
    }

    public void unlock(String lockKey) {
        RLock lock = getLock(lockKey);
        if (lock.isLocked()) {
            lock.unlock();
        }
    }

    public void unlock(RLock lock) {
        if (lock.isLocked()) {
            lock.unlock();
        }
    }

    public String getRlockKey(String lockKey) {
        return LOCKER_PREFIX + lockKey;
    }
}
