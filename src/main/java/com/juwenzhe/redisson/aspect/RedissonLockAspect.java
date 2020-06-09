package com.juwenzhe.redisson.aspect;

import com.juwenzhe.redisson.aspect.annotation.RedissonLock;
import com.juwenzhe.redisson.redlock.impl.RedissonDistributedLocker;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 分布式锁切面类
 * order的值越小，优先级越高！
 * 例如：在注解用于service类的方法时，优先级比@Transactional高，即当加上分布式锁之后再进行事务操作
 *
 * @author juwenzhe123@163.com
 * @date 2020/6/8 20:52
 */
@Aspect
@Component
@Order(1)
@Slf4j
public class RedissonLockAspect {

    @Autowired
    private RedissonDistributedLocker redissonDistributedLocker;

    /**
     * 处理锁
     *
     * @param joinPoint    切入点
     * @param redissonLock 分布式锁
     * @return
     */
    @Around("@annotation(redissonLock)")
    public Object around(ProceedingJoinPoint joinPoint, RedissonLock redissonLock) throws Throwable {

        Object obj = null;
        // 获取方法所有参数
        Object[] params = joinPoint.getArgs();

        log.debug("method params: {}", params);

        // 等待时长，默认5秒
        int waitTime = redissonLock.waitTime();
        // 锁默认释放时间
        int leaseTime = redissonLock.leaseTime();

        // 当前接口是否限制访问频率
        boolean rateLimited = redissonLock.rateLimited();

        int[] lockIndex = redissonLock.lockIndexs();
        String[] fieldNames = redissonLock.fieldNames();
        String lockParams = "";

        // 当锁2个及以上的参数时，fieldNames数量应该与lockIndexs一致
        if (fieldNames.length > 1 && lockIndex.length != fieldNames.length) {
            throw new Exception("lockIndexs与fieldNames数量不一致");
        }

        // 数组为空代表锁整个方法
        if (lockIndex.length > 0) {
            StringBuffer lockParamsBuffer = new StringBuffer();
            for (int i = 0; i < lockIndex.length; i++) {
                // 使用方法参数加锁，如果没指名用方法参数的某个属性加锁，那就对入参加锁
                if (fieldNames.length == 0 || fieldNames[i] == null || fieldNames[i].length() == 0) {
                    lockParamsBuffer.append(":$" + params[lockIndex[i]]);
                } else {
                    // 对低lockIndex[i]个方法参数的字段值，如User对象的(openid)加锁
                    Object lockParamValue = PropertyUtils.getSimpleProperty(params[lockIndex[i]], fieldNames[i]);
                    lockParamsBuffer.append(":$" + lockParamValue);
                }
            }
            lockParams = lockParamsBuffer.toString();
        }

        // 取key名
        String key = "$" + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName() + lockParams;

        RLock rLock = redissonDistributedLocker.getLock(key);
        log.debug("当前线程是否占着锁：{}，取到锁还存活的时间：{}", !rLock.isHeldByCurrentThread(), rLock.remainTimeToLive());

        // 拦住其他线程
        boolean isSuccess = redissonDistributedLocker.tryLock(rLock, waitTime, leaseTime, TimeUnit.SECONDS);

        if (isSuccess) {
            log.debug("获得锁[{}]", key);
            try {
                boolean excute = !rateLimited || rLock.getHoldCount() == 1;
                if (excute){
                    log.info("方法执行中:[{}]", key);
                    obj = joinPoint.proceed();
                } else {
                    obj = redissonLock.msg();
                }
            } finally {
                try {
                    if (!rateLimited) {
                        log.debug("方法执行完，释放锁[{}]", key);
                        redissonDistributedLocker.unlock(rLock);
                    }
                } catch (IllegalMonitorStateException e){
                    log.debug("相同条件请求线程[{}]已抢占锁key, 等待自动释放", params);
                }
            }
        } else {
            obj = redissonLock.msg();
        }
        return obj;
    }
}
