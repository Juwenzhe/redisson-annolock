package com.juwenzhe.redisson;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * api启动类
 * @author juwenzhe123@163.com
 * @date 2020/5/30 11:53
 */
@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
