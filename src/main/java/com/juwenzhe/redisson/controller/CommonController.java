package com.juwenzhe.redisson.controller;

import com.juwenzhe.redisson.aspect.annotation.RedissonLock;
import com.juwenzhe.redisson.entities.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 通用的controller
 * @author juwenzhe123@163.com
 * @date 2020/6/9 21:57
 */
@Slf4j
@RestController
@RequestMapping("dept")
public class CommonController {

    /**
     * 测试redlock
     * 1.没有参数时，锁整个方法
     * 使用场景：大并发访问减小接口压力，用户获得锁的条件是，上一个线程执行结束 或 锁自动释放时刻在其等待时间内
     * 缺点：没获取到锁的用户快速失败，体验不好
     * @return 压测结果：并发5请求，只有一个获取到锁，其他请求发现锁被占用5秒后才释放，直接返回
     */
    @RequestMapping(value = "/getToken1", method = RequestMethod.GET)
    @RedissonLock(waitTime = 1, leaseTime = 5, msg = "活动太火爆")
    public Object getToken1() {
        try {
            // 模拟方法执行10秒
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            log.info("线程sleep被打断..");
        }
        return "juwenzhe123321juwenzhe";
    }

    /**
     * 测试redlock
     * 2.将第一个参数对象的userId作为锁key，并限速，适用于接口防刷场景
     * @return 压测结果：5秒100请求，每个不同的用户只有一个请求成功
     */
    @RequestMapping(value = "/getToken2", method = RequestMethod.POST)
    @RedissonLock(rateLimited = true, waitTime = 0, leaseTime = 5, lockIndexs = {0}, fieldNames = {"userId"} , msg = "获取token过于频繁")
    public Object getToken2(@RequestBody User user) {
        return "juwenzhe123321juwenzhe" + user.getUserId();
    }

}
