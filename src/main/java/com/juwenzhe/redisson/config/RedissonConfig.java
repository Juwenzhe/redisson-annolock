package com.juwenzhe.redisson.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

/**
 * redisson配置类
 * @author juwenzhe123@163.com
 * @date 2020/6/8 20:45
 */
@Configuration
public class RedissonConfig {

    @Bean
    public RedissonClient redissonClient() throws IOException {
        Config config = Config.fromYAML(new ClassPathResource("conf/redisson-cluster.yml").getInputStream());
        return Redisson.create(config);
    }
}
