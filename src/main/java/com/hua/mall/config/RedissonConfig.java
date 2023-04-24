package com.hua.mall.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description Redisson 配置
 * @Author JingHua
 * @Date 2023/4/19 20:51
 */
@Configuration
public class RedissonConfig {

    @Bean
    public RedissonClient redisson() {
        return Redisson.create();
    }
}
