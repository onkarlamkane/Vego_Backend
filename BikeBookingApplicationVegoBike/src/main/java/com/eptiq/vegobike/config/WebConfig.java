//package com.eptiq.vegobike.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class WebConfig implements WebMvcConfigurer {
//
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/api/")
//                .allowedOrigins(
//                        "http://localhost:3000",    // React default
//                        "http://localhost:5173",    // Vite default
//                        "http://localhost:4173",    // Vite preview
//                        "http://localhost:8080"     // Alternative frontend port
//                )
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
//                .allowedHeaders("*")
//                .allowCredentials(true)
//                .maxAge(3600);
//    }
//
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        // Map /uploads/** URLs to the external uploads directory
//        registry.addResourceHandler("/uploads/")
//                .addResourceLocations("file:uploads/")
//                .setCachePeriod(3600); // Cache for 1 hour
//
//        // If you also want to serve from other directories
//        registry.addResourceHandler("/media/")
//                .addResourceLocations("file:media/")
//                .setCachePeriod(3600);
//    }
//
//}



package com.eptiq.vegobike.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir:/var/lib/uploads}")
    private String uploadDir;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        log.info("WEB_CONFIG - Configuring CORS mappings");

        registry.addMapping("/api/")
                .allowedOrigins(
                        "http://localhost:3000",
                        "http://localhost:5173",
                        "http://localhost:5174",
                        "http://localhost:8080",
                        "http://localhost:8081",
                        "http://127.0.0.1:5173",
                        "http://127.0.0.1:3000",
                        "https://api.eptiq.com",
                        "https://user.eptiq.com",
                        "https://admin.eptiq.com"

                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD")
                .allowedHeaders("*")
                .exposedHeaders("Authorization", "Content-Type", "X-Total-Count")
                .allowCredentials(true)
                .maxAge(3600);

        log.info("WEB_CONFIG - CORS configured for production and development origins");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("WEB_CONFIG - Configuring resource handlers");
        log.info("WEB_CONFIG - Upload directory: {}", uploadDir);

        // Production upload directory
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadDir + "/")
                .setCachePeriod(3600);

        // Development upload directory fallback
        registry.addResourceHandler("/media/")
                .addResourceLocations("file:media/")
                .setCachePeriod(3600);

        log.info("WEB_CONFIG - Resource handlers configured successfully");
    }
}