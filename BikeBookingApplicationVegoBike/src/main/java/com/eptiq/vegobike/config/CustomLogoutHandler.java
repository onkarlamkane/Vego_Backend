package com.eptiq.vegobike.config;

import com.eptiq.vegobike.model.User;
import com.eptiq.vegobike.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class CustomLogoutHandler implements LogoutHandler {

    private final UserRepository userRepository;

    public CustomLogoutHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void logout(HttpServletRequest request,
                       HttpServletResponse response,
                       Authentication authentication) {

        String clientIP = getClientIP(request);
        String userAgent = request.getHeader("User-Agent");

        log.info("LOGOUT_ATTEMPT - Processing logout request from IP: {}, UserAgent: {}",
                maskIP(clientIP), userAgent != null ? userAgent.substring(0, Math.min(userAgent.length(), 100)) : "Unknown");

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("LOGOUT_FAILED - Missing or invalid Authorization header from IP: {}", maskIP(clientIP));
            return;
        }

        String token = authHeader.substring(7);
        log.debug("Processing logout with token length: {}", token.length());

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();

            try {
                log.info("LOGOUT_PROCESSING - User: {}, IP: {}, Timestamp: {}",
                        maskEmail(username), maskIP(clientIP), LocalDateTime.now());

                // Fetch user from DB
                User user = userRepository.findByEmail(username)
                        .orElseThrow(() -> {
                            log.error("LOGOUT_ERROR - User not found in database: {}", maskEmail(username));
                            return new RuntimeException("User not found during logout");
                        });

                // Clear firebaseToken to logout
                if (token.equals(user.getFirebaseToken())) {
                    user.setFirebaseToken(null);
                    userRepository.save(user);

                    log.info("LOGOUT_SUCCESS - User: {}, UserID: {}, IP: {}, SessionCleared: true",
                            maskEmail(username), user.getId(), maskIP(clientIP));
                } else {
                    log.warn("LOGOUT_MISMATCH - Token mismatch for user: {}, IP: {}",
                            maskEmail(username), maskIP(clientIP));
                }

            } catch (Exception e) {
                log.error("LOGOUT_ERROR - Unexpected error during logout for user: {}, IP: {}, Error: {}",
                        maskEmail(username), maskIP(clientIP), e.getMessage(), e);
                // Continue with logout process even if DB operation fails
            }

        } else {
            log.warn("LOGOUT_INVALID - No valid authentication principal found, IP: {}", maskIP(clientIP));
        }

        log.info("LOGOUT_COMPLETED - Session cleanup completed for IP: {}", maskIP(clientIP));
    }

    private String getClientIP(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIP = request.getHeader("X-Real-IP");
        if (xRealIP != null && !xRealIP.isEmpty()) {
            return xRealIP;
        }

        return request.getRemoteAddr();
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "****";
        }
        String[] parts = email.split("@");
        return parts[0].substring(0, Math.min(2, parts[0].length())) + "****@" + parts[1];
    }

    private String maskIP(String ip) {
        if (ip == null || ip.isEmpty()) {
            return "****";
        }
        String[] parts = ip.split("\\.");
        if (parts.length == 4) {
            return parts[0] + "." + parts[1] + ".***." + parts[3];
        }
        return ip.substring(0, Math.min(4, ip.length())) + "****";
    }
}
