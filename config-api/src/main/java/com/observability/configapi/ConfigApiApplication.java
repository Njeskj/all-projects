package com.observability.configapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ConfigApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConfigApiApplication.class, args);
    }
}
