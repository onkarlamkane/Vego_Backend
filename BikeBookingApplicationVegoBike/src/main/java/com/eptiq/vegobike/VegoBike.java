//package com.eptiq.vegobike;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.boot.context.event.ApplicationFailedEvent;
//import org.springframework.boot.context.event.ApplicationReadyEvent;
//import org.springframework.context.ConfigurableApplicationContext;
//import org.springframework.context.event.EventListener;
//import org.springframework.core.env.Environment;
//import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
//
//import java.net.InetAddress;
//import java.time.LocalDateTime;
//import java.util.Arrays;
//
//@Slf4j
//@SpringBootApplication
//@EnableJpaAuditing
//public class VegoBike {
//
//    public static void main(String[] args) {
//        long startTime = System.currentTimeMillis();
//
//        log.info("APPLICATION_STARTUP_INITIATED - VegoBike Application startup initiated at {}",
//                LocalDateTime.now());
//
//        try {
//            // Log system information
//            logSystemInfo();
//
//            // Log startup arguments
//            if (args.length > 0) {
//                log.info("APPLICATION_STARTUP_ARGS - Startup arguments: {}", Arrays.toString(args));
//            }
//
//            // Start Spring Boot application
//            ConfigurableApplicationContext context = SpringApplication.run(VegoBike.class, args);
//
//            // Log application information after successful startup
//            logApplicationInfo(context, startTime);
//
//            // Add shutdown hook for graceful shutdown logging
//            addShutdownHook(context);
//
//        } catch (Exception e) {
//            // ✅ Handle DevTools SilentExitException properly
//            if (isSilentExitException(e)) {
//                log.debug("DEVTOOLS_RESTART - Spring DevTools is restarting the application (SilentExitException)");
//                return; // Don't treat as error, just return
//            }
//
//            // Handle real application failures
//            long failedTime = System.currentTimeMillis() - startTime;
//            log.error("APPLICATION_STARTUP_FAILED - VegoBike Application failed to start after {}ms, Error: {}",
//                    failedTime, e.getMessage(), e);
//            System.exit(1);
//        }
//    }
//
//    /**
//     * Check if the exception is a SilentExitException from DevTools
//     */
//    private static boolean isSilentExitException(Exception e) {
//        if (e == null) {
//            return false;
//        }
//
//        String exceptionName = e.getClass().getName();
//        String message = e.getMessage();
//
//        // Check for SilentExitException or DevTools restart related exceptions
//        return exceptionName.contains("SilentExitException") ||
//                exceptionName.contains("SilentExitExceptionHandler") ||
//                (message == null && exceptionName.contains("devtools"));
//    }
//
//    private static void logSystemInfo() {
//        try {
//            String javaVersion = System.getProperty("java.version");
//            String javaVendor = System.getProperty("java.vendor");
//            String osName = System.getProperty("os.name");
//            String osVersion = System.getProperty("os.version");
//            String osArch = System.getProperty("os.arch");
//            long maxMemory = Runtime.getRuntime().maxMemory() / (1024 * 1024); // MB
//            int availableProcessors = Runtime.getRuntime().availableProcessors();
//
//            log.info("SYSTEM_INFO - Java Version: {} ({}), OS: {} {} ({}), Max Memory: {}MB, Processors: {}",
//                    javaVersion, javaVendor, osName, osVersion, osArch, maxMemory, availableProcessors);
//
//        } catch (Exception e) {
//            log.warn("SYSTEM_INFO_ERROR - Could not retrieve system information: {}", e.getMessage());
//        }
//    }
//
//    private static void logApplicationInfo(ConfigurableApplicationContext context, long startTime) {
//        try {
//            Environment env = context.getEnvironment();
//            String applicationName = env.getProperty("spring.application.name", "VegoBike");
//            String[] activeProfiles = env.getActiveProfiles();
//            String serverPort = env.getProperty("server.port", "8080");
//            long startupTime = System.currentTimeMillis() - startTime;
//
//            // Get local server information
//            String hostAddress = InetAddress.getLocalHost().getHostAddress();
//            String hostName = InetAddress.getLocalHost().getHostName();
//
//            log.info("APPLICATION_STARTUP_SUCCESS - {} Application started successfully in {}ms",
//                    applicationName, startupTime);
//            log.info("APPLICATION_INFO - Host: {} ({}), Port: {}, Profiles: {}",
//                    hostName, hostAddress, serverPort,
//                    activeProfiles.length > 0 ? Arrays.toString(activeProfiles) : "[default]");
//
//            // Log application URLs
//            String protocol = env.getProperty("server.ssl.enabled", "false").equals("true") ? "https" : "http";
//            log.info("APPLICATION_URLS - Local: {}://localhost:{}, External: {}://{}:{}",
//                    protocol, serverPort, protocol, hostAddress, serverPort);
//
//            // Log database information (if available)
//            String datasourceUrl = env.getProperty("spring.datasource.url");
//            if (datasourceUrl != null) {
//                // Mask password in URL for security
//                String maskedUrl = datasourceUrl.replaceAll("password=[^&;]*", "password=****");
//                log.info("DATABASE_INFO - DataSource URL: {}", maskedUrl);
//            }
//
//            log.info("APPLICATION_READY - VegoBike Application is ready to serve requests");
//
//        } catch (Exception e) {
//            log.warn("APPLICATION_INFO_ERROR - Could not retrieve application information: {}", e.getMessage());
//        }
//    }
//
//    private static void addShutdownHook(ConfigurableApplicationContext context) {
//        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//            log.info("APPLICATION_SHUTDOWN_INITIATED - VegoBike Application shutdown initiated at {}",
//                    LocalDateTime.now());
//
//            try {
//                if (context.isActive()) {
//                    context.close();
//                }
//                log.info("APPLICATION_SHUTDOWN_SUCCESS - VegoBike Application shutdown completed successfully");
//            } catch (Exception e) {
//                log.error("APPLICATION_SHUTDOWN_ERROR - Error during application shutdown: {}", e.getMessage(), e);
//            }
//        }));
//    }
//
//    // Application lifecycle event listeners
//    @EventListener
//    public void handleApplicationReady(ApplicationReadyEvent event) {
//        log.info("APPLICATION_EVENT_READY - Application ready event received, all beans initialized and ready to serve requests");
//
//        // Log some application statistics
//        try {
//            long totalMemory = Runtime.getRuntime().totalMemory() / (1024 * 1024);
//            long freeMemory = Runtime.getRuntime().freeMemory() / (1024 * 1024);
//            long usedMemory = totalMemory - freeMemory;
//
//            log.info("MEMORY_INFO - Total: {}MB, Used: {}MB, Free: {}MB",
//                    totalMemory, usedMemory, freeMemory);
//
//        } catch (Exception e) {
//            log.debug("MEMORY_INFO_ERROR - Could not retrieve memory information: {}", e.getMessage());
//        }
//    }
//
//    @EventListener
//    public void handleApplicationFailed(ApplicationFailedEvent event) {
//        Throwable exception = event.getException();
//
//        // Don't log SilentExitException as application failure
//        if (!isSilentExitException((Exception) exception)) {
//            log.error("APPLICATION_EVENT_FAILED - Application failed to start: {}",
//                    exception.getMessage(), exception);
//        }
//    }
//}






package com.eptiq.vegobike;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.Arrays;

@Slf4j
@SpringBootApplication
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "com.eptiq.vegobike.repositories") // ✅ CRITICAL FIX
@EntityScan(basePackages = "com.eptiq.vegobike")
public class VegoBike {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        log.info("APPLICATION_STARTUP_INITIATED - VegoBike Application startup initiated at {}",
                LocalDateTime.now());

        try {
            // Log system information
            logSystemInfo();

            // Log startup arguments
            if (args.length > 0) {
                log.info("APPLICATION_STARTUP_ARGS - Startup arguments: {}", Arrays.toString(args));
            }

            // Start Spring Boot application
            ConfigurableApplicationContext context = SpringApplication.run(VegoBike.class, args);

            // Log application information after successful startup
            logApplicationInfo(context, startTime);

            // Add shutdown hook for graceful shutdown logging
            addShutdownHook(context);

        } catch (Exception e) {
            // ✅ Handle DevTools SilentExitException properly
            if (isSilentExitException(e)) {
                log.debug("DEVTOOLS_RESTART - Spring DevTools is restarting the application (SilentExitException)");
                return; // Don't treat as error, just return
            }

            // Handle real application failures
            long failedTime = System.currentTimeMillis() - startTime;
            log.error("APPLICATION_STARTUP_FAILED - VegoBike Application failed to start after {}ms, Error: {}",
                    failedTime, e.getMessage(), e);
            System.exit(1);
        }
    }

    /**
     * Check if the exception is a SilentExitException from DevTools
     */
    private static boolean isSilentExitException(Exception e) {
        if (e == null) {
            return false;
        }

        String exceptionName = e.getClass().getName();
        String message = e.getMessage();

        // Check for SilentExitException or DevTools restart related exceptions
        return exceptionName.contains("SilentExitException") ||
                exceptionName.contains("SilentExitExceptionHandler") ||
                (message == null && exceptionName.contains("devtools"));
    }

    private static void logSystemInfo() {
        try {
            String javaVersion = System.getProperty("java.version");
            String javaVendor = System.getProperty("java.vendor");
            String osName = System.getProperty("os.name");
            String osVersion = System.getProperty("os.version");
            String osArch = System.getProperty("os.arch");
            long maxMemory = Runtime.getRuntime().maxMemory() / (1024 * 1024); // MB
            int availableProcessors = Runtime.getRuntime().availableProcessors();

            log.info("SYSTEM_INFO - Java Version: {} ({}), OS: {} {} ({}), Max Memory: {}MB, Processors: {}",
                    javaVersion, javaVendor, osName, osVersion, osArch, maxMemory, availableProcessors);

        } catch (Exception e) {
            log.warn("SYSTEM_INFO_ERROR - Could not retrieve system information: {}", e.getMessage());
        }
    }

    private static void logApplicationInfo(ConfigurableApplicationContext context, long startTime) {
        try {
            Environment env = context.getEnvironment();
            String applicationName = env.getProperty("spring.application.name", "VegoBike");
            String[] activeProfiles = env.getActiveProfiles();
            String serverPort = env.getProperty("server.port", "8080");
            long startupTime = System.currentTimeMillis() - startTime;

            // Get local server information
            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            String hostName = InetAddress.getLocalHost().getHostName();

            log.info("APPLICATION_STARTUP_SUCCESS - {} Application started successfully in {}ms",
                    applicationName, startupTime);
            log.info("APPLICATION_INFO - Host: {} ({}), Port: {}, Profiles: {}",
                    hostName, hostAddress, serverPort,
                    activeProfiles.length > 0 ? Arrays.toString(activeProfiles) : "[default]");

            // Log application URLs
            String protocol = env.getProperty("server.ssl.enabled", "false").equals("true") ? "https" : "http";
            log.info("APPLICATION_URLS - Local: {}://localhost:{}, External: {}://{}:{}",
                    protocol, serverPort, protocol, hostAddress, serverPort);

            // Log database information (if available)
            String datasourceUrl = env.getProperty("spring.datasource.url");
            if (datasourceUrl != null) {
                // Mask password in URL for security
                String maskedUrl = datasourceUrl.replaceAll("password=[^&;]*", "password=****");
                log.info("DATABASE_INFO - DataSource URL: {}", maskedUrl);
            }

            log.info("APPLICATION_READY - VegoBike Application is ready to serve requests");

        } catch (Exception e) {
            log.warn("APPLICATION_INFO_ERROR - Could not retrieve application information: {}", e.getMessage());
        }
    }

    private static void addShutdownHook(ConfigurableApplicationContext context) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("APPLICATION_SHUTDOWN_INITIATED - VegoBike Application shutdown initiated at {}",
                    LocalDateTime.now());

            try {
                if (context.isActive()) {
                    context.close();
                }
                log.info("APPLICATION_SHUTDOWN_SUCCESS - VegoBike Application shutdown completed successfully");
            } catch (Exception e) {
                log.error("APPLICATION_SHUTDOWN_ERROR - Error during application shutdown: {}", e.getMessage(), e);
            }
        }));
    }

    // Application lifecycle event listeners
    @EventListener
    public void handleApplicationReady(ApplicationReadyEvent event) {
        log.info("APPLICATION_EVENT_READY - Application ready event received, all beans initialized and ready to serve requests");

        // Log some application statistics
        try {
            long totalMemory = Runtime.getRuntime().totalMemory() / (1024 * 1024);
            long freeMemory = Runtime.getRuntime().freeMemory() / (1024 * 1024);
            long usedMemory = totalMemory - freeMemory;

            log.info("MEMORY_INFO - Total: {}MB, Used: {}MB, Free: {}MB",
                    totalMemory, usedMemory, freeMemory);

        } catch (Exception e) {
            log.debug("MEMORY_INFO_ERROR - Could not retrieve memory information: {}", e.getMessage());
        }
    }

    @EventListener
    public void handleApplicationFailed(ApplicationFailedEvent event) {
        Throwable exception = event.getException();

        // Don't log SilentExitException as application failure
        if (!isSilentExitException((Exception) exception)) {
            log.error("APPLICATION_EVENT_FAILED - Application failed to start: {}",
                    exception.getMessage(), exception);
        }
    }
}

