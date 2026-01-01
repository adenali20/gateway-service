package com.adenali.gateway_service.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class EnvLogger {

    @Autowired
    private Environment env;

    @PostConstruct
    public void logEnv() {
        System.out.println("JWT_SECRET_KEY = " + env.getProperty("JWT_SECRET_KEY"));
        System.out.println("SPRING_PROFILES_ACTIVE = " + env.getProperty("spring.profiles.active"));
        System.out.println("SERVICE_URL = " + env.getProperty("SERVICE_URL"));
    }
}
