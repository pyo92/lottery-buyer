package com.lottog.purchaser.config;

import com.lottog.purchaser.dto.request.LoginRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class AppConfig {

    @Bean("loginInfoMap")
    public ConcurrentHashMap<String, LoginRequest> loginInfoMap() {
        return new ConcurrentHashMap<>();
    }
}
