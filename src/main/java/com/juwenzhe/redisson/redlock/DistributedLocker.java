package com.juwenzhe.redisson.redlock;

import org.redisson.api.RLock;

import java.util.concurrent.TimeUnit;

/**
 * @author juwenzhe123@163.com
 * @date 2020/6/8 20:59
 */
public interface DistributedLocker {

    /**
     * 获得锁
     */
    RLock getLock(String lockKey);

    /**
     * 锁lockKey
     */
    RLock lock(String lockKey);

    /**
     * 锁lockKey timeout
     */
    RLock lock(String lockKey, long timeout);

    /**
     * 锁lockKey
     */
    RLock lock(String lockKey, long timeout, TimeUnit unit);

    /**
     * 尝试锁
     * @return 是否成功
     */
    boolean tryLock(String lockKey, long waitTime, long leaseTime, TimeUnit unit);

    /**
     * 尝试锁
     * @return 是否成功
     */
    boolean tryLock(RLock lock, long waitTime, long leaseTime, TimeUnit unit);

    /**
     * 解锁
     */
    void unlock(String lockKey);

    /**
     * 解锁
     */
    void unlock(RLock lock);
}
