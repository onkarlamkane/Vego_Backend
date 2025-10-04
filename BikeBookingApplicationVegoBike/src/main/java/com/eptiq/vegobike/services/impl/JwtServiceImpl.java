package com.eptiq.vegobike.services.impl;

import com.eptiq.vegobike.model.User;
import com.eptiq.vegobike.repositories.UserRepository;
import com.eptiq.vegobike.services.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Service
public class JwtServiceImpl implements JwtService {

    private final String secretKey;
    private final long expirationTime;
    private final UserRepository userRepository;

    public JwtServiceImpl(
            @Value("${jwt.secret-key:vegobike-super-secret-key-for-production-environment-that-should-be-at-least-256-bits-long}") String secretKey,
            @Value("${jwt.expiration:86400000}") long expirationTime, // 24 hours
            UserRepository userRepository) {

        if (secretKey == null || secretKey.trim().isEmpty()) {
            log.error("JWT_SERVICE_INIT_ERROR - JWT secret key is null or empty");
            throw new IllegalArgumentException("JWT secret key cannot be null or empty. Please check your application properties file.");
        }

        if (expirationTime <= 0) {
            log.error("JWT_SERVICE_INIT_ERROR - JWT expiration time is invalid: {}", expirationTime);
            throw new IllegalArgumentException("JWT expiration time must be positive. Current value: " + expirationTime);
        }

        if (userRepository == null) {
            log.error("JWT_SERVICE_INIT_ERROR - UserRepository is null");
            throw new IllegalArgumentException("UserRepository cannot be null");
        }

        this.secretKey = secretKey;
        this.expirationTime = expirationTime;
        this.userRepository = userRepository;

        log.info("JWT_SERVICE_INIT - JWT Service initialized successfully");
        log.info("JWT_SERVICE_INIT - Secret key length: {} characters", secretKey.length());
        log.info("JWT_SERVICE_INIT - Expiration time: {} ms ({}h {}m)",
                expirationTime, expirationTime / 3600000, (expirationTime % 3600000) / 60000);
    }

    @PostConstruct
    public void validateConfiguration() {
        try {
            log.info("JWT_SERVICE_POST_CONSTRUCT - Validating JWT configuration");

            SecretKey testKey = getSignInKey();
            if (testKey == null) {
                throw new IllegalStateException("Failed to create JWT signing key");
            }

            log.info("JWT_SERVICE_POST_CONSTRUCT - JWT configuration validated successfully");
        } catch (Exception e) {
            log.error("JWT_SERVICE_POST_CONSTRUCT_ERROR - JWT configuration validation failed: {}", e.getMessage(), e);
            throw new IllegalStateException("JWT service configuration is invalid: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public String generateToken(UserDetails userDetails) {
        if (userDetails == null) {
            throw new IllegalArgumentException("UserDetails cannot be null");
        }

        String username = userDetails.getUsername();
        String correlationId = generateCorrelationId();

        log.info("JWT_TOKEN_GENERATION_START - CorrelationId: {}, User: {}, Timestamp: {}",
                correlationId, maskUsername(username), LocalDateTime.now());

        try {
            // Create minimal claims to keep token size small [web:189][web:174]
            Map<String, Object> extraClaims = new HashMap<>();

            if (userDetails instanceof User user) {
                // Only include essential claims to minimize token size
                extraClaims.put("uid", user.getId()); // Shorter key names
                extraClaims.put("role", user.getRoleId() != null ? user.getRoleId() : 3);

                // Only add storeId for store managers
                if (user.getRoleId() != null && user.getRoleId() == 2 && user.getStoreId() != null) {
                    extraClaims.put("sid", user.getStoreId()); // Shorter key name
                }

                log.info("🔐 JWT_CUSTOM_CLAIMS - CorrelationId: {}, UserId: {}, Role: {}",
                        correlationId, user.getId(), user.getRoleId());
            }

            return generateToken(extraClaims, userDetails);

        } catch (Exception e) {
            log.error("JWT_TOKEN_GENERATION_FAILED - CorrelationId: {}, User: {}, Error: {}",
                    correlationId, maskUsername(username), e.getMessage(), e);
            throw new RuntimeException("Failed to generate JWT token: " + e.getMessage(), e);
        }
    }

    @Override
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, expirationTime);
    }

    @Override
    public String extractUsername(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Token cannot be null or empty");
        }

        String correlationId = generateCorrelationId();

        try {
            log.debug("JWT_USERNAME_EXTRACTION_START - CorrelationId: {}, TokenLength: {}",
                    correlationId, token.length());

            String username = extractClaim(token, Claims::getSubject);

            log.debug("JWT_USERNAME_EXTRACTION_SUCCESS - CorrelationId: {}, Username: {}",
                    correlationId, maskUsername(username));
            return username;

        } catch (ExpiredJwtException e) {
            log.warn("JWT_USERNAME_EXTRACTION_EXPIRED - CorrelationId: {}, ExpiredAt: {}, Error: {}",
                    correlationId, e.getClaims().getExpiration(), e.getMessage());
            throw e;
        } catch (JwtException e) {
            log.error("JWT_USERNAME_EXTRACTION_INVALID - CorrelationId: {}, Error: {}",
                    correlationId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("JWT_USERNAME_EXTRACTION_FAILED - CorrelationId: {}, Error: {}",
                    correlationId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public String extractPhoneNumber(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Token cannot be null or empty");
        }

        String correlationId = generateCorrelationId();

        try {
            log.debug("JWT_PHONE_EXTRACTION_START - CorrelationId: {}, TokenLength: {}",
                    correlationId, token.length());

            // For phone-based login, subject contains phone number
            String phoneNumber = extractClaim(token, Claims::getSubject);

            log.debug("JWT_PHONE_EXTRACTION_SUCCESS - CorrelationId: {}, Phone: {}",
                    correlationId, maskPhoneNumber(phoneNumber));
            return phoneNumber;

        } catch (Exception e) {
            log.error("JWT_PHONE_EXTRACTION_FAILED - CorrelationId: {}, Error: {}",
                    correlationId, e.getMessage());
            throw e;
        }
    }

    public Long extractUserId(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Token cannot be null or empty");
        }

        try {
            return extractClaim(token, claims -> claims.get("uid", Long.class)); // Use shorter key name
        } catch (Exception e) {
            log.error("JWT_USERID_EXTRACTION_FAILED - Error: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        if (token == null || userDetails == null) {
            log.warn("JWT_TOKEN_VALIDATION - Token or UserDetails is null");
            return false;
        }

        String username = userDetails.getUsername();
        String correlationId = generateCorrelationId();

        log.debug("JWT_TOKEN_VALIDATION_START - CorrelationId: {}, User: {}, TokenLength: {}",
                correlationId, maskUsername(username), token.length());

        try {
            final String extractedUsername = extractUsername(token);
            boolean usernameMatches = extractedUsername.equals(username);
            boolean tokenNotExpired = !isTokenExpired(token);

            // OPTIONAL: Skip database token check for better performance [web:169][web:186]
            // JWT tokens are stateless by design and don't need database persistence
            boolean tokenStoredInDatabase = true;

            if (userDetails instanceof User user) {
                // Only check DB token if you really need it
                // For stateless JWT, you can remove this check entirely
                tokenStoredInDatabase = token.equals(user.getFirebaseToken());

                Long tokenUserId = extractUserId(token);
                boolean userIdMatches = tokenUserId != null && tokenUserId.equals(user.getId());

                log.debug("JWT_TOKEN_VALIDATION_CHECKS - CorrelationId: {}, User: {}, UserID: {}, UsernameMatch: {}, UserIdMatch: {}, NotExpired: {}, StoredInDB: {}",
                        correlationId, maskUsername(username), user.getId(),
                        usernameMatches, userIdMatches, tokenNotExpired, tokenStoredInDatabase);

                boolean isValid = usernameMatches && userIdMatches && tokenNotExpired && tokenStoredInDatabase;

                if (isValid) {
                    log.info("JWT_TOKEN_VALIDATION_SUCCESS - CorrelationId: {}, User: {}, UserID: {}, AllChecksPass: true",
                            correlationId, maskUsername(username), user.getId());
                } else {
                    log.warn("JWT_TOKEN_VALIDATION_FAILED - CorrelationId: {}, User: {}, UserID: {}, UsernameMatch: {}, UserIdMatch: {}, NotExpired: {}, StoredInDB: {}",
                            correlationId, maskUsername(username), user.getId(),
                            usernameMatches, userIdMatches, tokenNotExpired, tokenStoredInDatabase);
                }

                return isValid;
            }

            boolean isValid = usernameMatches && tokenNotExpired;

            if (isValid) {
                log.info("JWT_TOKEN_VALIDATION_SUCCESS - CorrelationId: {}, User: {}, AllChecksPass: true",
                        correlationId, maskUsername(username));
            } else {
                log.warn("JWT_TOKEN_VALIDATION_FAILED - CorrelationId: {}, User: {}, UsernameMatch: {}, NotExpired: {}",
                        correlationId, maskUsername(username), usernameMatches, tokenNotExpired);
            }

            return isValid;

        } catch (ExpiredJwtException e) {
            log.warn("JWT_TOKEN_VALIDATION_EXPIRED - CorrelationId: {}, User: {}, ExpiredAt: {}",
                    correlationId, maskUsername(username), e.getClaims().getExpiration());
            return false;
        } catch (Exception e) {
            log.error("JWT_TOKEN_VALIDATION_ERROR - CorrelationId: {}, User: {}, Error: {}",
                    correlationId, maskUsername(username), e.getMessage());
            return false;
        }
    }

    @Override
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        try {
            final Claims claims = extractAllClaims(token);
            return claimsResolver.apply(claims);
        } catch (ExpiredJwtException e) {
            log.debug("JWT_CLAIM_EXTRACTION_EXPIRED - Token expired, but extracting claim anyway");
            return claimsResolver.apply(e.getClaims());
        } catch (Exception e) {
            log.error("JWT_CLAIM_EXTRACTION_FAILED - Error extracting claim: {}", e.getMessage());
            throw e;
        }
    }

    // CRITICAL FIX: Modified to avoid storing full JWT token in database
    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        Date issuedAt = new Date();
        Date expirationDate = new Date(System.currentTimeMillis() + expiration);

        String token = Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(issuedAt)
                .expiration(expirationDate)
                .issuer("VegoBike-API")
                .signWith(getSignInKey())
                .compact();

        // OPTION 1: Don't store JWT token in database (recommended for stateless JWT) [web:169]
        // JWT tokens are designed to be stateless and self-contained

        // OPTION 2: Store only a token identifier instead of full JWT
        if (userDetails instanceof User user) {
            try {
                // Store a shorter identifier instead of the full token
                String tokenId = "tok_" + System.currentTimeMillis() + "_" + user.getId();
                user.setFirebaseToken(tokenId);
                user.setUpdatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
                userRepository.save(user);
                log.info("JWT_TOKEN_ID_STORED - Token ID stored in database for user ID: {}", user.getId());
            } catch (Exception e) {
                log.warn("JWT_TOKEN_STORAGE_FAILED - Failed to store token ID for user ID: {}, Error: {}",
                        user.getId(), e.getMessage());
                // Don't fail token generation if storage fails
            }
        }

        return token;
    }

    public boolean isTokenExpired(String token) {
        try {
            Date expiration = extractExpiration(token);
            Date now = new Date();
            boolean expired = expiration.before(now);

            log.debug("JWT_TOKEN_EXPIRY_CHECK - IsExpired: {}, ExpirationDate: {}, CurrentTime: {}",
                    expired, expiration, now);
            return expired;

        } catch (ExpiredJwtException e) {
            log.debug("JWT_TOKEN_ALREADY_EXPIRED - ExpiredAt: {}", e.getClaims().getExpiration());
            return true;
        } catch (Exception e) {
            log.error("JWT_TOKEN_EXPIRY_CHECK_ERROR - Error: {}", e.getMessage());
            return true;
        }
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.debug("JWT_CLAIMS_EXTRACTION_EXPIRED - Token expired: {}", e.getMessage());
            throw e;
        } catch (JwtException e) {
            log.error("JWT_CLAIMS_EXTRACTION_INVALID - Invalid JWT structure: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("JWT_CLAIMS_EXTRACTION_FAILED - Unexpected error: {}", e.getMessage(), e);
            throw e;
        }
    }

    private SecretKey getSignInKey() {
        try {
            if (secretKey == null || secretKey.trim().isEmpty()) {
                throw new IllegalStateException("JWT secret key is not configured");
            }

            byte[] keyBytes = Decoders.BASE64.decode(secretKey);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (IllegalArgumentException e) {
            log.error("JWT_KEY_GENERATION_FAILED - Invalid Base64 secret key: {}", e.getMessage());
            throw new RuntimeException("JWT secret key is not valid Base64: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("JWT_KEY_GENERATION_FAILED - Error creating signing key: {}", e.getMessage());
            throw new RuntimeException("Failed to create JWT signing key: " + e.getMessage(), e);
        }
    }

    // Utility methods for logging
    private String generateCorrelationId() {
        return java.util.UUID.randomUUID().toString().substring(0, 8);
    }

    private String maskUsername(String username) {
        if (username == null) {
            return "";
        }

        if (username.contains("@")) {
            String[] parts = username.split("@");
            return parts[0].substring(0, Math.min(2, parts[0].length())) + "*@" + parts[1];
        }

        if (username.length() >= 4) {
            return username.substring(0, 2) + "" + username.substring(username.length() - 2);
        }

        return "*";
    }

    private String maskPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() < 4) {
            return "";
        }
        return phoneNumber.substring(0, 2) + "" + phoneNumber.substring(phoneNumber.length() - 2);
    }


    /**
     * Extract customer ID from HttpServletRequest
     * @param request HttpServletRequest containing Authorization header
     * @return Long customer ID extracted from JWT token
     * @throws RuntimeException if token is invalid or customer ID not found
     */
    public Long extractCustomerIdFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.error("🚫 JWT_CUSTOMER_ID_EXTRACTION_FAILED - Authorization header is missing or invalid");
            throw new RuntimeException("Authorization header is missing or invalid");
        }

        String token = authHeader.substring(7); // Remove "Bearer " prefix
        String correlationId = generateCorrelationId();

        try {
            log.debug("🔐 JWT_CUSTOMER_ID_EXTRACTION_START - CorrelationId: {}, TokenLength: {}",
                    correlationId, token.length());

            Long customerId = extractUserId(token);

            if (customerId == null) {
                log.error("🚫 JWT_CUSTOMER_ID_EXTRACTION_FAILED - CorrelationId: {}, Customer ID not found in token",
                        correlationId);
                throw new RuntimeException("Customer ID not found in token");
            }

            log.debug("🔐 JWT_CUSTOMER_ID_EXTRACTION_SUCCESS - CorrelationId: {}, CustomerID: {}",
                    correlationId, customerId);
            return customerId;

        } catch (ExpiredJwtException e) {
            log.error("🚫 JWT_CUSTOMER_ID_EXTRACTION_EXPIRED - CorrelationId: {}, ExpiredAt: {}",
                    correlationId, e.getClaims().getExpiration());
            throw new RuntimeException("Token has expired");
        } catch (JwtException e) {
            log.error("🚫 JWT_CUSTOMER_ID_EXTRACTION_INVALID - CorrelationId: {}, Error: {}",
                    correlationId, e.getMessage());
            throw new RuntimeException("Invalid token: " + e.getMessage());
        } catch (Exception e) {
            log.error("🚫 JWT_CUSTOMER_ID_EXTRACTION_FAILED - CorrelationId: {}, Error: {}",
                    correlationId, e.getMessage());
            throw new RuntimeException("Failed to extract customer ID from token: " + e.getMessage());
        }
    }

}