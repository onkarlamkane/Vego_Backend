package com.eptiq.vegobike.controllers;

import com.eptiq.vegobike.dtos.*;
import com.eptiq.vegobike.mappers.UserMapper;
import com.eptiq.vegobike.mappers.UserMapperImpl;
import com.eptiq.vegobike.model.User;
import com.eptiq.vegobike.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final UserService authService;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public AuthenticationController(UserService authService, PasswordEncoder passwordEncoder , UserMapper userMapper) {
        this.authService = authService;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    // Health check endpoint
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "VegoBike Authentication Service",
                "timestamp", LocalDateTime.now(),
                "version", "2.0.0"
        ));
    }

    // Test connection endpoint
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testConnection() {
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Backend server is running",
                "timestamp", LocalDateTime.now(),
                "version", "2.0.0",
                "developmentMode", true
        ));
    }

    // ---------- Registration OTP Endpoints ----------
    @PostMapping(value = "/send-registration-otp", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<Map<String, Object>> sendRegistrationOTP(
            @RequestPart("data") String requestJson,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
            HttpServletRequest httpRequest) {

        try {
            log.info("🚀 CONTROLLER - Registration OTP request received from IP: {}",
                    getClientIP(httpRequest));

            Map<String, String> response = authService.sendRegistrationOTP(requestJson, profileImage);

            // Enhanced response for development
            Map<String, Object> enhancedResponse = new HashMap<>(response);
            enhancedResponse.put("timestamp", LocalDateTime.now());
            enhancedResponse.put("endpoint", "send-registration-otp");

            if ("true".equals(response.get("status"))) {
                log.info("✅ CONTROLLER - Registration OTP sent successfully");
                return ResponseEntity.ok(enhancedResponse);
            } else {
                log.warn("⚠ CONTROLLER - Registration OTP failed: {}", response.get("message"));
                return ResponseEntity.badRequest().body(enhancedResponse);
            }

        } catch (Exception e) {
            log.error("💥 CONTROLLER - Registration OTP failed: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "false",
                    "message", "Failed to send registration OTP: " + e.getMessage(),
                    "timestamp", LocalDateTime.now(),
                    "error", "INTERNAL_SERVER_ERROR"
            ));
        }
    }

    @PostMapping("/verify-registration-otp")
    public ResponseEntity<Map<String, Object>> verifyRegistrationOTP(
            @Valid @RequestBody OTPVerificationRequest request,
            BindingResult bindingResult,
            HttpServletRequest httpRequest) {

        try {
            log.info("🔐 CONTROLLER - Registration OTP verification for phone: {}",
                    maskPhoneNumber(request.getPhoneNumber()));

            // Validation check
            if (bindingResult.hasErrors()) {
                Map<String, String> errors = bindingResult.getFieldErrors().stream()
                        .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
                log.warn("⚠ CONTROLLER - Registration OTP validation failed: {}", errors);
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "VALIDATION_FAILED",
                        "message", "Please check your input fields",
                        "details", errors,
                        "timestamp", LocalDateTime.now()
                ));
            }

            AuthResponse response = authService.registerWithOtp(request);
            log.info("✅ CONTROLLER - Registration successful");

            return ResponseEntity.ok(Map.of(
                    "message", "Registration successful",
                    "token", response.token(),
                    "timestamp", LocalDateTime.now(),
                    "success", true
            ));

        } catch (Exception e) {
            log.error("💥 CONTROLLER - Registration OTP verification failed: {}", e.getMessage(), e);
            throw e; // Let GlobalExceptionHandler handle it
        }
    }

    // ---------- Login OTP Endpoints ----------
    @PostMapping("/send-login-otp")
    public ResponseEntity<Map<String, Object>> sendLoginOTP(
            @Valid @RequestBody PhoneNumberRequest request,
            BindingResult bindingResult,
            HttpServletRequest httpRequest) {

        try {
            log.info("📱 CONTROLLER - Login OTP request for phone: {} from IP: {}",
                    maskPhoneNumber(request.getPhoneNumber()), getClientIP(httpRequest));

            // Validation check
            if (bindingResult.hasErrors()) {
                Map<String, String> errors = bindingResult.getFieldErrors().stream()
                        .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
                log.warn("⚠ CONTROLLER - Login OTP validation failed: {}", errors);
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "VALIDATION_FAILED",
                        "message", "Please check your input fields",
                        "details", errors,
                        "timestamp", LocalDateTime.now()
                ));
            }

            Map<String, String> response = authService.sendLoginOTP(request);

            // Enhanced response for development
            Map<String, Object> enhancedResponse = new HashMap<>(response);
            enhancedResponse.put("timestamp", LocalDateTime.now());
            enhancedResponse.put("endpoint", "send-login-otp");

            if ("true".equals(response.get("status"))) {
                log.info("✅ CONTROLLER - Login OTP sent successfully");
                return ResponseEntity.ok(enhancedResponse);
            } else {
                log.warn("⚠ CONTROLLER - Login OTP failed: {}", response.get("message"));
                return ResponseEntity.badRequest().body(enhancedResponse);
            }

        } catch (Exception e) {
            log.error("💥 CONTROLLER - Login OTP failed: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "false",
                    "message", "Failed to send login OTP: " + e.getMessage(),
                    "timestamp", LocalDateTime.now(),
                    "error", "INTERNAL_SERVER_ERROR"
            ));
        }
    }

    @PostMapping("/verify-login-otp")
    public ResponseEntity<Map<String, Object>> verifyLoginOTP(
            @Valid @RequestBody OTPVerificationRequest request,
            BindingResult bindingResult,
            HttpServletRequest httpRequest) {

        try {
            log.info("🔐 CONTROLLER - Login OTP verification for phone: {} from IP: {}",
                    maskPhoneNumber(request.getPhoneNumber()), getClientIP(httpRequest));

            // Validation check
            if (bindingResult.hasErrors()) {
                Map<String, String> errors = bindingResult.getFieldErrors().stream()
                        .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
                log.warn("⚠ CONTROLLER - Login OTP validation failed: {}", errors);
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "VALIDATION_FAILED",
                        "message", "Please check your input fields",
                        "details", errors,
                        "timestamp", LocalDateTime.now()
                ));
            }

            // Input validation
            if (request.getPhoneNumber() == null || request.getPhoneNumber().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "VALIDATION_ERROR",
                        "message", "Phone number is required",
                        "timestamp", LocalDateTime.now()
                ));
            }

            if (request.getOtp() == null || request.getOtp().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "VALIDATION_ERROR",
                        "message", "OTP is required",
                        "timestamp", LocalDateTime.now()
                ));
            }

            // OTP format validation
            if (!request.getOtp().matches("\\d{4}")) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "INVALID_OTP_FORMAT",
                        "message", "OTP must be a 4-digit number",
                        "timestamp", LocalDateTime.now()
                ));
            }

            AuthResponse response = authService.verifyLoginOTP(request);
            log.info("✅ CONTROLLER - Login successful for phone: {}",
                    maskPhoneNumber(request.getPhoneNumber()));

            return ResponseEntity.ok(Map.of(
                    "message", "Login successful",
                    "token", response.token(),
                    "timestamp", LocalDateTime.now(),
                    "success", true
            ));

        } catch (Exception e) {
            log.error("💥 CONTROLLER - Login OTP verification failed: {}", e.getMessage(), e);
            throw e; // Let GlobalExceptionHandler handle it
        }
    }

    // ---------- Debug Endpoint (Development Only) ----------
    @GetMapping("/debug/otp/{phoneNumber}")
    public ResponseEntity<Map<String, Object>> debugOTP(@PathVariable String phoneNumber) {
        try {
            log.info("🔍 CONTROLLER - Debug OTP request for phone: {}", maskPhoneNumber(phoneNumber));

            // This would require adding a debug method to UserService
            Map<String, Object> debugInfo = Map.of(
                    "phoneNumber", maskPhoneNumber(phoneNumber),
                    "message", "Debug info - check server logs for detailed Redis status",
                    "developmentMode", true,
                    "defaultOTP", "1234",
                    "timestamp", LocalDateTime.now()
            );

            return ResponseEntity.ok(debugInfo);

        } catch (Exception e) {
            log.error("💥 CONTROLLER - Debug OTP failed: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "DEBUG_FAILED",
                    "message", e.getMessage(),
                    "timestamp", LocalDateTime.now()
            ));
        }
    }

    // ---------- Admin and Store Manager Endpoints (unchanged) ----------
    @PostMapping("/admin/register")
    public ResponseEntity<Map<String, Object>> registerAdmin(
            @Valid @RequestBody AdminRegistrationRequest request,
            BindingResult bindingResult,
            HttpServletRequest httpRequest) {

        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage())
            );
            log.warn("Admin registration validation failed: {}", errors);
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "VALIDATION_FAILED",
                    "message", "Please check your input fields",
                    "details", errors,
                    "timestamp", LocalDateTime.now()
            ));
        }

        try {
            AuthResponse response = authService.registerAdmin(request);
            log.info("Admin registration successful for email: {}", request.getEmail());
            return ResponseEntity.ok(Map.of(
                    "message", "Admin registration successful",
                    "token", response.token(),
                    "timestamp", LocalDateTime.now()
            ));
        } catch (Exception e) {
            log.error("Admin registration failed: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "INTERNAL_SERVER_ERROR",
                    "message", "Registration service temporarily unavailable",
                    "timestamp", LocalDateTime.now()
            ));
        }
    }

    @PostMapping("/admin/login")
    public ResponseEntity<Map<String, Object>> adminLogin(
            @Valid @RequestBody AdminLoginRequest request,
            BindingResult bindingResult,
            HttpServletRequest httpRequest) {

        String clientIP = getClientIP(httpRequest);
        String email = request != null ? request.getEmail() : "unknown";

        log.info("Admin login attempt for email: {} from IP: {}", email, maskIP(clientIP));

        try {
            // Validation errors
            if (bindingResult.hasErrors()) {
                Map<String, String> errors = new HashMap<>();
                bindingResult.getFieldErrors()
                        .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
                log.warn("Admin login validation failed for email: {} - Errors: {}", email, errors);
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "VALIDATION_FAILED",
                        "message", "Please check your input fields",
                        "details", errors,
                        "timestamp", LocalDateTime.now()
                ));
            }

            // Additional input validation
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "VALIDATION_FAILED",
                        "message", "Email is required",
                        "timestamp", LocalDateTime.now()
                ));
            }

            if (request.getPassword() == null || request.getPassword().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "VALIDATION_FAILED",
                        "message", "Password is required",
                        "timestamp", LocalDateTime.now()
                ));
            }

            // Find user
            User user = authService.findByUsername(request.getEmail().trim());
            if (user == null) {
                log.warn("Admin login failed - User not found: {}", email);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                        "error", "INVALID_CREDENTIALS",
                        "message", "Invalid email or password",
                        "timestamp", LocalDateTime.now()
                ));
            }

            // Verify password
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                log.warn("Admin login failed - Invalid password for user: {}", email);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                        "error", "INVALID_CREDENTIALS",
                        "message", "Invalid email or password",
                        "timestamp", LocalDateTime.now()
                ));
            }

            // Check admin role
            if (user.getRoleId() != 1) {
                log.warn("Admin login failed - Insufficient privileges. User: {}, Role: {}", email, user.getRoleId());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                        "error", "ACCESS_DENIED",
                        "message", "Admin privileges required",
                        "timestamp", LocalDateTime.now()
                ));
            }

            // Authenticate user
            AuthResponse response = authService.authenticate(user);
            log.info("Admin login successful for user: {} (ID: {})", email, user.getId());

            return ResponseEntity.ok(Map.of(
                    "message", "Login successful",
                    "token", response.token(),
                    "user", Map.of(
                            "id", user.getId(),
                            "name", user.getName(),
                            "email", user.getEmail(),
                            "roleId", user.getRoleId()
                    ),
                    "timestamp", LocalDateTime.now()
            ));

        } catch (Exception e) {
            log.error("Admin login error for email: {} - Error: {}", email, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "INTERNAL_SERVER_ERROR",
                    "message", "Authentication service temporarily unavailable",
                    "timestamp", LocalDateTime.now()
            ));
        }
    }

    @PostMapping("/store-manager/login")
    public ResponseEntity<Map<String, Object>> storeManagerLogin(
            @Valid @RequestBody AdminLoginRequest request,
            BindingResult bindingResult,
            HttpServletRequest httpRequest) {

        String clientIP = getClientIP(httpRequest);
        String email = request != null ? request.getEmail() : "unknown";

        log.info("Store manager login attempt for email: {} from IP: {}", email, maskIP(clientIP));

        try {
            // Validation errors
            if (bindingResult.hasErrors()) {
                Map<String, String> errors = bindingResult.getFieldErrors().stream()
                        .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
                log.warn("Store manager login validation failed for email: {} - Errors: {}", email, errors);
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "VALIDATION_FAILED",
                        "message", "Please check your input fields",
                        "details", errors,
                        "timestamp", LocalDateTime.now()
                ));
            }

            // Additional input validation
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "VALIDATION_FAILED",
                        "message", "Email is required",
                        "timestamp", LocalDateTime.now()
                ));
            }

            if (request.getPassword() == null || request.getPassword().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "VALIDATION_FAILED",
                        "message", "Password is required",
                        "timestamp", LocalDateTime.now()
                ));
            }

            // Find user
            User user = authService.findByUsername(request.getEmail().trim());
            if (user == null) {
                log.warn("Store manager login failed - User not found: {}", email);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                        "error", "INVALID_CREDENTIALS",
                        "message", "Invalid email or password",
                        "timestamp", LocalDateTime.now()
                ));
            }

            // Verify password
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                log.warn("Store manager login failed - Invalid password for user: {}", email);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                        "error", "INVALID_CREDENTIALS",
                        "message", "Invalid email or password",
                        "timestamp", LocalDateTime.now()
                ));
            }

            // Check store manager role
            if (user.getRoleId() != 2) {
                log.warn("Store manager login failed - Insufficient privileges. User: {}, Role: {}", email, user.getRoleId());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                        "error", "ACCESS_DENIED",
                        "message", "Store manager privileges required",
                        "timestamp", LocalDateTime.now()
                ));
            }

            // Authenticate user
            AuthResponse response = authService.authenticate(user);
            log.info("Store manager login successful for user: {} (ID: {})", email, user.getId());

            return ResponseEntity.ok(Map.of(
                    "message", "Login successful",
                    "token", response.token(),
                    "user", Map.of(
                            "id", user.getId(),
                            "name", user.getName(),
                            "email", user.getEmail(),
                            "roleId", user.getRoleId(),
                            "storeId",  user.getStoreId()
                    ),
                    "timestamp", LocalDateTime.now()
            ));

        } catch (Exception e) {
            log.error("Store manager login error for email: {} - Error: {}", email, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "INTERNAL_SERVER_ERROR",
                    "message", "Authentication service temporarily unavailable",
                    "timestamp", LocalDateTime.now()
            ));
        }
    }

    @GetMapping("/store-managers")
    public ResponseEntity<Map<String, Object>> getAllStoreManagers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            var pageable = PageRequest.of(page, size);
            var managersPage = authService.getAllStoreManagers(pageable);

            return ResponseEntity.ok(Map.of(
                    "count", managersPage.getTotalElements(),
                    "totalPages", managersPage.getTotalPages(),
                    "currentPage", page,
                    "pageSize", size,
                    "storeManagers", managersPage.getContent(),
                    "timestamp", LocalDateTime.now()
            ));
        } catch (Exception e) {
            log.error("Error fetching store managers: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "INTERNAL_SERVER_ERROR",
                    "message", "Unable to fetch store managers",
                    "timestamp", LocalDateTime.now()
            ));
        }
    }

    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        try {
            // 🧩 Sort descending by given field (default: id)
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
            var usersPage = authService.getAllUsers(pageable);

            return ResponseEntity.ok(Map.of(
                    "count", usersPage.getTotalElements(),
                    "totalPages", usersPage.getTotalPages(),
                    "currentPage", page,
                    "pageSize", size,
                    "sortBy", sortBy,
                    "sortOrder", "DESC",
                    "users", usersPage.getContent(),
                    "timestamp", LocalDateTime.now()
            ));
        } catch (Exception e) {
            log.error("Error fetching users: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "INTERNAL_SERVER_ERROR",
                    "message", "Unable to fetch users",
                    "timestamp", LocalDateTime.now()
            ));
        }
    }


    // ---------- Utility Methods ----------
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
            return "unknown";
        }
        String[] parts = ip.split("\\.");
        if (parts.length == 4) {
            return parts[0] + "." + parts[1] + ".*." + parts[3];
        }
        return ip.substring(0, Math.min(4, ip.length())) + "*";
    }

    private String maskPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() < 4) {
            return "";
        }
        return phoneNumber.substring(0, 2) + "" + phoneNumber.substring(phoneNumber.length() - 2);
    }

    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getCurrentUserProfile(HttpServletRequest request) {
        log.info("🌐 GET_CURRENT_USER_PROFILE - Profile request received");

        try {
            // Extract token from Authorization header
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.warn("🔐 GET_CURRENT_USER_PROFILE - No valid authorization header found");
                return ResponseEntity.status(401).body(Map.of(
                        "success", false,
                        "error", "UNAUTHORIZED",
                        "message", "Authorization header required",
                        "timestamp", LocalDateTime.now()
                ));
            }

            String token = authHeader.substring(7); // Remove "Bearer " prefix
            log.info("🔐 GET_CURRENT_USER_PROFILE - Token extracted from header");

            UserProfileDTO profile = authService.getUserProfile(token);
            log.info("✅ GET_CURRENT_USER_PROFILE - Profile retrieved successfully for user ID: {}", profile.getId());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Profile retrieved successfully",
                    "data", profile,
                    "timestamp", LocalDateTime.now()
            ));

        } catch (Exception e) {
            log.error("💥 GET_CURRENT_USER_PROFILE - Error: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "error", "INTERNAL_SERVER_ERROR",
                    "message", e.getMessage(),
                    "timestamp", LocalDateTime.now()
            ));
        }
    }

    /**
     * Update current user profile
     * Editable fields: name, address, accountNumber, ifsc, upiId, profile
     */
    @PutMapping("/profile")
    public ResponseEntity<Map<String, Object>> updateCurrentUserProfile(
            HttpServletRequest request,
            @Valid @RequestBody UserProfileUpdateRequest updateRequest) {
        log.info("🌐 UPDATE_CURRENT_USER_PROFILE - Profile update request received");

        try {
            // Extract token and get user ID
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.warn("🔐 UPDATE_CURRENT_USER_PROFILE - No valid authorization header found");
                return ResponseEntity.status(401).body(Map.of(
                        "success", false,
                        "error", "UNAUTHORIZED",
                        "message", "Authorization header required",
                        "timestamp", LocalDateTime.now()
                ));
            }

            String token = authHeader.substring(7);
            Long userId = authService.getUserIdFromToken(token);
            log.info("🔐 UPDATE_CURRENT_USER_PROFILE - User ID extracted from token: {}", userId);

            UserProfileDTO updatedProfile = authService.updateUserProfile(userId, updateRequest);
            log.info("✅ UPDATE_CURRENT_USER_PROFILE - Profile updated successfully for user ID: {}", userId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Profile updated successfully",
                    "data", updatedProfile,
                    "timestamp", LocalDateTime.now()
            ));

        } catch (Exception e) {
            log.error("💥 UPDATE_CURRENT_USER_PROFILE - Error: {}", e.getMessage(), e);
            return ResponseEntity.status(400).body(Map.of(
                    "success", false,
                    "error", "BAD_REQUEST",
                    "message", e.getMessage(),
                    "timestamp", LocalDateTime.now()
            ));
        }
    }

    /**
     * Get user profile by ID (admin use)
     */
    @GetMapping("/profile/{userId}")
    public ResponseEntity<Map<String, Object>> getUserProfileById(@PathVariable Long userId) {
        log.info("🌐 GET_USER_PROFILE_BY_ID - Profile request for user ID: {}", userId);

        try {
            UserProfileDTO profile = authService.getUserProfileById(userId);
            log.info("✅ GET_USER_PROFILE_BY_ID - Profile retrieved successfully for user ID: {}", userId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Profile retrieved successfully",
                    "data", profile,
                    "timestamp", LocalDateTime.now()
            ));

        } catch (Exception e) {
            log.error("💥 GET_USER_PROFILE_BY_ID - Error for user ID {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(404).body(Map.of(
                    "success", false,
                    "error", "NOT_FOUND",
                    "message", e.getMessage(),
                    "timestamp", LocalDateTime.now()
            ));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpServletRequest request) {
        try {
            log.info("👤 AUTH_LOGOUT - Logging out user and clearing JWT");

            String authHeader = request.getHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                log.info("🔐 AUTH_LOGOUT - Token extracted: {}...", token.substring(0, Math.min(20, token.length())));

                // Optional: Blacklist token in Redis
                // tokenBlacklistService.blacklistToken(token);
            }

            log.info("✅ AUTH_LOGOUT - Server logout successful");

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Logged out successfully",
                    "timestamp", LocalDateTime.now()
            ));

        } catch (Exception e) {
            log.error("💥 AUTH_LOGOUT - Error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Logout failed",
                    "error", e.getMessage(),
                    "timestamp", LocalDateTime.now()
            ));
        }
    }

    @GetMapping("/search")
    public List<UserProfileDTO> searchUsers(@RequestParam(required = false) String searchText) {
        return authService.searchUsers(searchText);
    }

    @GetMapping("/by-phone/{phoneNumber}")
    public ResponseEntity<?> getUserByPhone(@PathVariable String phoneNumber) {
        User user = authService.getUserByPhoneNumber(phoneNumber);
        return ResponseEntity.ok(userMapper.toUserProfileDTO(user));
    }
}