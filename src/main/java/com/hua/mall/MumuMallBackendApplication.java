package com.hua.mall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * @author hua
 */
@SpringBootApplication
@EnableCaching
public class MumuMallBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(MumuMallBackendApplication.class, args);
    }

}
