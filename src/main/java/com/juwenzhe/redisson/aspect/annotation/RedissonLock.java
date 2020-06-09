package com.juwenzhe.redisson.aspect.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 分布式锁注解
 * 使用例子：
 * @RedissonLock 锁整个方法
 * @RedissonLock(lockIndexs = 0, rateLimited = true) 锁第一个参数, 并限速
 * @RedissonLock(lockIndexs = {0,1}, rateLimited = true) 锁第一个参数和第二个参数组合, 并限速
 * @RedissonLock(lockIndexs = {1,2}) 锁第二个参数和第三个参数组合
 * @RedissonLock(lockIndexs = 0,fieldNames = "transId") 锁第一个参数属性等于transId
 * @RedissonLock(lockIndexs = {0,1},fieldNames = {"transId","id"}) 锁第一个参数属性等于transId和第二个参数属性等于id的组合
 *
 * @author juwenzhe123@163.com
 * @date 2020/6/8 20:25
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedissonLock {
    /**
     * 要锁的参数角标
     */
    int[] lockIndexs() default {};

    /**
     * 要锁的参数的属性名
     */
    String[] fieldNames() default {};

    /**
     * 等待多久（单位：秒）
     */
    int waitTime() default 5;

    /**
     * 锁多久后自动释放（单位：秒）
     */
    int leaseTime() default 5;

    /**
     * 是否用于限速场景，为true时，方法执行结束不释放rlock
     */
    boolean rateLimited() default false;

    /**
     * 未取到锁时提示信息
     */
    String msg() default "操作频繁，请稍后重试";
}
