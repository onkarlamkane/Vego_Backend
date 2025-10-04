package com.eptiq.vegobike.filter;

import com.eptiq.vegobike.services.JwtService;
import com.eptiq.vegobike.services.impl.UserServiceImpl;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserServiceImpl userService;

    // ✅ FIXED: Use constructor injection instead of setter injection
    public JwtAuthenticationFilter(JwtService jwtService, UserServiceImpl userService) {
        this.jwtService = jwtService;
        this.userService = userService;
        log.info("JWT_FILTER_INIT - JwtAuthenticationFilter created with constructor injection");
    }

    @PostConstruct
    public void validateDependencies() {
        if (jwtService == null) {
            log.error("JWT_FILTER_ERROR - JwtService dependency was not injected properly");
            throw new IllegalStateException("JwtService dependency not satisfied");
        }

        if (userService == null) {
            log.error("JWT_FILTER_ERROR - UserService dependency was not injected properly");
            throw new IllegalStateException("UserService dependency not satisfied");
        }

        log.info("JWT_FILTER_INIT - All dependencies validated successfully");
        log.info("JWT_FILTER_INIT - JwtService: {}", jwtService.getClass().getSimpleName());
        log.info("JWT_FILTER_INIT - UserService: {}", userService.getClass().getSimpleName());
    }

    @Override
    protected void doFilterInternal(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain) throws ServletException, IOException {

        String correlationId = generateCorrelationId();
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        String clientIP = getClientIP(request);

        // ✅ Add correlation ID to MDC for this request
        MDC.put("correlationId", correlationId);
        MDC.put("clientIP", maskIP(clientIP));

        try {
            // Skip processing for public endpoints
            if (isPublicEndpoint(requestURI)) {
                log.debug("JWT_FILTER - Skipping public endpoint: {} {}", method, requestURI);
                filterChain.doFilter(request, response);
                return;
            }

            log.debug("JWT_FILTER_START - Processing request: {} {} from IP: {}",
                    method, requestURI, maskIP(clientIP));

            final String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.debug("JWT_FILTER - No valid Authorization header found for: {} {}", method, requestURI);
                filterChain.doFilter(request, response);
                return;
            }

            final String jwt = authHeader.substring(7);

            if (jwt.trim().isEmpty()) {
                log.warn("JWT_FILTER - Empty JWT token found, IP: {}, URI: {}", maskIP(clientIP), requestURI);
                filterChain.doFilter(request, response);
                return;
            }

            log.debug("JWT_FILTER - Token found, length: {} chars", jwt.length());

            final String username = jwtService.extractUsername(jwt);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                log.debug("JWT_FILTER - Validating token for user: {}", maskUsername(username));

                try {
                    UserDetails userDetails = userService.loadUserByUsername(username);

                    if (jwtService.isTokenValid(jwt, userDetails)) {
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities()
                                );

                        authToken.setDetails(
                                new WebAuthenticationDetailsSource().buildDetails(request)
                        );

                        SecurityContextHolder.getContext().setAuthentication(authToken);

                        log.info("JWT_AUTHENTICATION_SUCCESS - User: {}, IP: {}, URI: {}, CorrelationId: {}",
                                maskUsername(username), maskIP(clientIP), requestURI, correlationId);
                    } else {
                        log.warn("JWT_AUTHENTICATION_FAILED - Invalid token for user: {}, IP: {}, URI: {}, CorrelationId: {}",
                                maskUsername(username), maskIP(clientIP), requestURI, correlationId);
                    }
                } catch (Exception userLoadException) {
                    log.error("JWT_FILTER_USER_LOAD_ERROR - Failed to load user: {}, IP: {}, URI: {}, CorrelationId: {}, Error: {}",
                            maskUsername(username), maskIP(clientIP), requestURI, correlationId, userLoadException.getMessage());
                }
            } else if (username == null) {
                log.warn("JWT_TOKEN_INVALID - Could not extract username from token, IP: {}, URI: {}, CorrelationId: {}",
                        maskIP(clientIP), requestURI, correlationId);
            }

        } catch (Exception e) {
            log.error("JWT_FILTER_ERROR - Authentication failed for IP: {}, URI: {}, CorrelationId: {}, Error: {}",
                    maskIP(clientIP), requestURI, correlationId, e.getMessage());
            SecurityContextHolder.clearContext();
        }

        log.debug("JWT_FILTER_END - Continuing filter chain for: {} {}, CorrelationId: {}",
                method, requestURI, correlationId);
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        boolean skip = isPublicEndpoint(path);

        if (skip) {
            log.debug("JWT_FILTER_SKIP - Public endpoint detected: {}", path);
        }

        return skip;
    }

    private boolean isPublicEndpoint(String requestURI) {
        if (requestURI == null) {
            return false;
        }

        // ✅ Enhanced public endpoint patterns
        return requestURI.equals("/actuator/health") ||
                requestURI.equals("/error") ||
                requestURI.startsWith("/actuator/") ||
                requestURI.startsWith("/api/auth/") ||
                requestURI.startsWith("/uploads/") ||
                requestURI.startsWith("/api/models/") ||  // ✅ Allow model endpoints
                requestURI.startsWith("/api/stores/") ||  // ✅ Allow store endpoints
                requestURI.startsWith("/api/categories/") ||
                requestURI.startsWith("/api/brands/") ||
                requestURI.startsWith("/static/") ||
                requestURI.startsWith("/public/") ||
                requestURI.startsWith("/css/") ||
                requestURI.startsWith("/js/") ||
                requestURI.startsWith("/images/") ||
                requestURI.equals("/favicon.ico");
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

    private String maskIP(String ip) {
        if (ip == null || ip.isEmpty()) {
            return "";
        }

        if (ip.contains(".") && ip.split("\\.").length == 4) {
            String[] parts = ip.split("\\.");
            return parts[0] + "." + parts[1] + ".*." + parts[3];
        }

        return ip.length() > 8 ? ip.substring(0, 4) + "" + ip.substring(ip.length() - 4) : "";
    }

    private String maskUsername(String username) {
        if (username == null) {
            return "";
        }

        if (username.contains("@")) {
            String[] parts = username.split("@");
            return parts[0].substring(0, Math.min(2, parts[0].length())) + "@" + parts[1];
        }

        return username.length() >= 4
                ? username.substring(0, 2) + "" + username.substring(username.length() - 2)
                : "";
    }

    private String generateCorrelationId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }




}