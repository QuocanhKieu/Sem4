package com.devteria.identity.configuration;

import com.devteria.identity.service.FcmTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StartupRunner implements CommandLineRunner {

    private final FcmTokenService fcmTokenService;

    @Override
    public void run(String... args) {
        fcmTokenService.testRedis();
    }
}
