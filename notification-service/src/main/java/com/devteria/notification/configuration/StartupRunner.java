package com.devteria.notification.configuration;

import com.devteria.notification.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StartupRunner implements CommandLineRunner {

    private final RedisService redisService;

    @Override
    public void run(String... args) {
        redisService.testRedis();
    }
}
