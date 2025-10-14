package com.eptiq.vegobike.services;

import com.eptiq.vegobike.model.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * JWT Service Interface for VegoBike Authentication System
 *
 * Provides comprehensive JWT token management including:
 * - Token generation with custom user claims
 * - Token validation and verification
 * - Claims extraction (username, phone, userId, role, etc.)
 * - User-specific token operations
 *
 * @author VegoBike Team
 * @version 2.0
 * @since 2025-09-27
 */
public interface JwtService {

    // ========================================
    // CORE TOKEN OPERATIONS
    // ========================================

    /**
     * Generate JWT token for authenticated user with custom claims
     *
     * @param userDetails The authenticated user details
     * @return JWT token string
     * @throws IllegalArgumentException if userDetails is null
     */
    String generateToken(UserDetails userDetails);

    /**
     * Generate JWT token specifically for User entity (convenience method)
     *
     * @param user The User entity
     * @return JWT token string
     */
    default String generateToken(User user) {
        return generateToken((UserDetails) user);
    }

    /**
     * Validate JWT token against user details
     *
     * @param token JWT token to validate
     * @param userDetails User details to validate against
     * @return true if token is valid, false otherwise
     */
    boolean isTokenValid(String token, UserDetails userDetails);

    /**
     * Check if token is valid without user details (expiry check only)
     *
     * @param token JWT token to validate
     * @return true if token is not expired, false otherwise
     */
    default boolean isTokenValid(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    // ========================================
    // STANDARD CLAIMS EXTRACTION
    // ========================================

    /**
     * Extract username (subject) from JWT token
     *
     * @param token JWT token
     * @return Username/subject from token
     * @throws IllegalArgumentException if token is null or empty
     */
    String extractUsername(String token);

    /**
     * Extract phone number from JWT token
     *
     * @param token JWT token
     * @return Phone number from custom phoneNumber claim
     * @throws IllegalArgumentException if token is null or empty
     */
    String extractPhoneNumber(String token);

    // ========================================
    // CUSTOM CLAIMS EXTRACTION
    // ========================================

    /**
     * Extract user ID from JWT token custom claims
     *
     * @param token JWT token
     * @return User ID as Long, null if not present
     */
    default Long extractUserId(String token) {
        try {
            return extractClaim(token, claims -> claims.get("userId", Long.class));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Extract email from JWT token custom claims
     *
     * @param token JWT token
     * @return Email address, null if not present
     */
    default String extractEmail(String token) {
        try {
            return extractClaim(token, claims -> claims.get("email", String.class));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Extract user name from JWT token custom claims
     *
     * @param token JWT token
     * @return User's name, null if not present
     */
    default String extractName(String token) {
        try {
            return extractClaim(token, claims -> claims.get("name", String.class));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Extract role ID from JWT token custom claims
     *
     * @param token JWT token
     * @return Role ID as Integer, defaults to 3 (USER) if not present
     */
    default Integer extractRoleId(String token) {
        try {
            Integer roleId = extractClaim(token, claims -> claims.get("roleId", Integer.class));
            return roleId != null ? roleId : 3; // Default to USER role
        } catch (Exception e) {
            return 3; // Default to USER role
        }
    }

    /**
     * Extract role name from JWT token custom claims
     *
     * @param token JWT token
     * @return Role name (ADMIN, STORE_MANAGER, USER), defaults to "USER"
     */
    default String extractRole(String token) {
        try {
            String role = extractClaim(token, claims -> claims.get("role", String.class));
            return role != null ? role : "USER";
        } catch (Exception e) {
            return "USER"; // Default role
        }
    }

    /**
     * Extract store ID from JWT token custom claims (for store managers)
     *
     * @param token JWT token
     * @return Store ID as Long, null if not present
     */
    default Long extractStoreId(String token) {
        try {
            return extractClaim(token, claims -> claims.get("storeId", Long.class));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Extract account status from JWT token custom claims
     *
     * @param token JWT token
     * @return isActive status as Integer, defaults to 1 (active)
     */
    default Integer extractIsActive(String token) {
        try {
            Integer isActive = extractClaim(token, claims -> claims.get("isActive", Integer.class));
            return isActive != null ? isActive : 1;
        } catch (Exception e) {
            return 1; // Default to active
        }
    }

    /**
     * Extract document verification status from JWT token custom claims
     *
     * @param token JWT token
     * @return isDocumentVerified status as Integer, defaults to 0 (not verified)
     */
    default Integer extractIsDocumentVerified(String token) {
        try {
            Integer isVerified = extractClaim(token, claims -> claims.get("isDocumentVerified", Integer.class));
            return isVerified != null ? isVerified : 0;
        } catch (Exception e) {
            return 0; // Default to not verified
        }
    }

    // ========================================
    // UTILITY METHODS
    // ========================================

    /**
     * Get user ID directly from token (convenience method)
     *
     * @param token JWT token
     * @return User ID as Long, null if extraction fails
     */
    default Long getUserIdFromToken(String token) {
        return extractUserId(token);
    }

    /**
     * Check if user is admin based on token
     *
     * @param token JWT token
     * @return true if user has admin role (roleId = 1)
     */
    default boolean isAdmin(String token) {
        Integer roleId = extractRoleId(token);
        return roleId != null && roleId == 1;
    }

    /**
     * Check if user is store manager based on token
     *
     * @param token JWT token
     * @return true if user has store manager role (roleId = 2)
     */
    default boolean isStoreManager(String token) {
        Integer roleId = extractRoleId(token);
        return roleId != null && roleId == 2;
    }

    /**
     * Check if user is regular user based on token
     *
     * @param token JWT token
     * @return true if user has user role (roleId = 3 or null)
     */
    default boolean isUser(String token) {
        Integer roleId = extractRoleId(token);
        return roleId == null || roleId == 3;
    }

    /**
     * Check if token is expired
     *
     * @param token JWT token
     * @return true if token is expired
     */
    default boolean isTokenExpired(String token) {
        try {
            java.util.Date expiration = extractClaim(token, claims -> claims.getExpiration());
            return expiration.before(new java.util.Date());
        } catch (Exception e) {
            return true; // Consider expired if we can't check
        }
    }

    // ========================================
    // ADVANCED OPERATIONS (TO BE IMPLEMENTED)
    // ========================================

    /**
     * Extract a specific claim from JWT token using a claims resolver function
     *
     * @param <T> The type of claim value expected
     * @param token JWT token
     * @param claimsResolver Function to resolve claims
     * @return Claim value of type T
     */
    default <T> T extractClaim(String token, java.util.function.Function<io.jsonwebtoken.Claims, T> claimsResolver) {
        // This should be implemented in the concrete class
        throw new UnsupportedOperationException("extractClaim method must be implemented in concrete class");
    }

    /**
     * Generate token with custom claims
     *
     * @param extraClaims Additional claims to include in token
     * @param userDetails User details
     * @return JWT token string
     */
    default String generateToken(java.util.Map<String, Object> extraClaims, UserDetails userDetails) {
        // This should be implemented in the concrete class
        throw new UnsupportedOperationException("generateToken with custom claims must be implemented in concrete class");
    }

    // ========================================
    // USER CONTEXT OPERATIONS
    // ========================================

    /**
     * Extract complete user context from token
     *
     * @param token JWT token
     * @return UserContext containing all user information
     */
    default UserContext extractUserContext(String token) {
        return UserContext.builder()
                .userId(extractUserId(token))
                .username(extractUsername(token))
                .phoneNumber(extractPhoneNumber(token))
                .email(extractEmail(token))
                .name(extractName(token))
                .roleId(extractRoleId(token))
                .role(extractRole(token))
                .storeId(extractStoreId(token))
                .isActive(extractIsActive(token))
                .isDocumentVerified(extractIsDocumentVerified(token))
                .build();
    }

    /**
     * User context data class for convenient access to all user information
     */
    @lombok.Data
    @lombok.Builder
    class UserContext {
        private Long userId;
        private String username;
        private String phoneNumber;
        private String email;
        private String name;
        private Integer roleId;
        private String role;
        private Long storeId;
        private Integer isActive;
        private Integer isDocumentVerified;

        public boolean isAdmin() {
            return roleId != null && roleId == 1;
        }

        public boolean isStoreManager() {
            return roleId != null && roleId == 2;
        }

        public boolean isUser() {
            return roleId == null || roleId == 3;
        }

        public boolean isActive() {
            return isActive != null && isActive == 1;
        }

        public boolean isDocumentVerified() {
            return isDocumentVerified != null && isDocumentVerified == 1;
        }

        public boolean hasValidStoreId() {
            return storeId != null && storeId > 0;
        }
    }

    Long extractCustomerIdFromRequest(HttpServletRequest request);
}
