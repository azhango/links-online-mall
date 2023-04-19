package com.hua.mall.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @Description todo
 * @Author JingHua
 * @Date 2023/4/19 20:51
 */
@Component
public class RedissonConfig {

    @Bean
    public RedissonClient redisson() {
        return Redisson.create();
    }
}
