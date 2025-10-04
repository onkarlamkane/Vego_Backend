package com.eptiq.vegobike.config;

import com.eptiq.vegobike.filter.JwtAuthenticationFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        log.info("🔧 SECURITY_CONFIG - Configuring CORS");

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",
                "http://localhost:5173",
                "http://localhost:5174",
                "http://localhost:4173",
                "http://localhost:8080",
                "http://127.0.0.1:5173",
                "http://127.0.0.1:3000",
                "https://user.eptiq.com",
                "https://admin.eptiq.com",
                "https://api.eptiq.com"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(Arrays.asList("Authorization", "X-Request-ID"));
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        log.info("✅ SECURITY_CONFIG - CORS configured successfully");
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {

        log.info("🔧 SECURITY_CONFIG - Configuring security filter chain");

        http.csrf(AbstractHttpConfigurer::disable)

                // Enable CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Configure authorization
                .authorizeHttpRequests(req -> {
                    log.debug("🔐 SECURITY_CONFIG - Configuring authorization");
                    req
                            // ✅ PUBLIC ENDPOINTS
                            .requestMatchers(
                                    "/api/auth/health",
                                    "/api/auth/test",
                                    "/api/auth/send-registration-otp",
                                    "/api/auth/verify-registration-otp",
                                    "/api/auth/send-login-otp",
                                    "/api/auth/verify-login-otp",
                                    "/api/auth/admin/login",
                                    "/api/auth/admin/register",
                                    "/api/auth/store-manager/login",
                                    "/api/**",
                                    "/api/auth/debug/",
                                    "/error",
                                    "/media/**",
                                    "/uploads/**"
                            ).permitAll()

                            // ✅ AUTHENTICATED ENDPOINTS
                            .requestMatchers(

                                    "/api/auth/logout"
                            ).authenticated()

                            .anyRequest().authenticated();
                })

                // Stateless session (JWT)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Security headers
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.deny())
                        .contentTypeOptions(contentTypeOptions -> {
                        })
                        .httpStrictTransportSecurity(hsts -> hsts
                                .maxAgeInSeconds(31536000)
                                .includeSubDomains(true)
                                .preload(true)
                        )
                        .referrerPolicy(referrerPolicy ->
                                referrerPolicy.policy(org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
                        )
                )

                // Add JWT filter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        log.info("✅ SECURITY_CONFIG - Security filter chain configured");
        return http.build();
    }
}