package com.eptiq.vegobike.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@Configuration
public class PasswordConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        log.info("SECURITY_CONFIG - Initializing BCryptPasswordEncoder with strength 12");

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

        log.info("SECURITY_CONFIG - PasswordEncoder bean created successfully with enterprise-level security");
        return encoder;
    }
}
