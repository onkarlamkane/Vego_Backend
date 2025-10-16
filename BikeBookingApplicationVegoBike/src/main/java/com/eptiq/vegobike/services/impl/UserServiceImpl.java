package com.eptiq.vegobike.services.impl;

import com.eptiq.vegobike.dtos.*;
import com.eptiq.vegobike.exceptions.CustomAuthenticationException;
import com.eptiq.vegobike.exceptions.InvalidOTPException;
import com.eptiq.vegobike.exceptions.UserNotFoundException;
import com.eptiq.vegobike.exceptions.UserRegistrationException;
import com.eptiq.vegobike.mappers.UserMapper;
import com.eptiq.vegobike.model.User;
import com.eptiq.vegobike.repositories.UserRepository;
import com.eptiq.vegobike.services.JwtService;
import com.eptiq.vegobike.services.MSG91OTPService;
import com.eptiq.vegobike.services.UserService;
import com.eptiq.vegobike.utils.ImageUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final MSG91OTPService msg91OTPService;
    private final ImageUtils imageUtils ;

    // Environment configuration for development bypass
    @Value("${app.environment:development}")
    private String environment;

    @Value("${app.otp.development.bypass:true}")
    private boolean developmentOTPBypass;

    // TTLs and keys
    private static final Duration OTP_TTL = Duration.ofMinutes(5);
    private static final Duration RESEND_LOCK_TTL = Duration.ofSeconds(30);
    private static final Duration FAILED_ATTEMPTS_TTL = Duration.ofMinutes(15);

    private static final String OTP_KEY_PREFIX = "OTP_";
    private static final String RESEND_LOCK_PREFIX = "OTP_RESEND_LOCK_";
    private static final String REGISTRATION_DATA_PREFIX = "REG_DATA_";
    private static final String FAILED_ATTEMPTS_PREFIX = "OTP_FAILED_";

    // Development OTP for testing - Always use 1234
    private static final String DEVELOPMENT_OTP = "1234";
    private static final int MAX_OTP_ATTEMPTS = 3;

    // Constructor with conditional MSG91OTPService dependency
    public UserServiceImpl(UserRepository userRepository,
                           JwtService jwtService,
                           UserMapper userMapper,
                           PasswordEncoder passwordEncoder,
                           RedisTemplate<String, String> redisTemplate,
                           ObjectMapper objectMapper,
                           Optional<MSG91OTPService> msg91OTPService ,
                           ImageUtils imageUtils) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.msg91OTPService = msg91OTPService.orElse(null);
        this.imageUtils = imageUtils;

    }

    // ---------- Registration OTP (send) ----------
    @Override
    public Map<String, String> sendRegistrationOTP(String requestJson, MultipartFile profileImage) {
        try {
            log.info("🚀 REGISTRATION_OTP - Starting OTP sending process");

            RegistrationRequest registrationRequest = objectMapper.readValue(requestJson, RegistrationRequest.class);
            String phone = registrationRequest.getPhoneNumber();

            log.info("📞 REGISTRATION_OTP - Phone: {}", maskPhoneNumber(phone));

            // Validate phone number format
            if (!isValidPhoneNumber(phone)) {
                log.warn("📞 REGISTRATION_OTP - Invalid phone number format: {}", maskPhoneNumber(phone));
                return Map.of(
                        "status", "false",
                        "message", "Invalid phone number format"
                );
            }

            // Check if user already exists
            if (userRepository.existsByPhoneNumber(phone)) {
                log.warn("📞 REGISTRATION_OTP - Phone number already registered: {}", maskPhoneNumber(phone));
                return Map.of(
                        "status", "false",
                        "message", "Phone number already registered"
                );
            }

            // Check resend lock
            String resendKey = RESEND_LOCK_PREFIX + phone;
            Boolean hasLock = redisTemplate.hasKey(resendKey);
            if (Boolean.TRUE.equals(hasLock)) {
                Long ttl = redisTemplate.getExpire(resendKey, TimeUnit.SECONDS);
                log.warn("⏰ REGISTRATION_OTP - Resend lock active for {} seconds", ttl);
                return Map.of(
                        "status", "false",
                        "message", "Please wait " + ttl + " seconds before resending OTP"
                );
            }

            // Handle profile image if provided
            if (profileImage != null && !profileImage.isEmpty()) {
                log.info("📸 REGISTRATION_OTP - Profile image received: {}", profileImage.getOriginalFilename());
                String profileImagePath = imageUtils.saveProfileImage(profileImage, 0); // 0 = userId not yet available
                registrationRequest.setProfileImage(profileImage);
                log.info("📸 REGISTRATION_OTP - Profile image saved at: {}", profileImagePath);
            }

            // Always use default OTP "1234" for development
            String otp = DEVELOPMENT_OTP;
            log.info("🧪 REGISTRATION_OTP - Using development OTP: {}", otp);

            // Store the OTP in Redis with explicit TTL
            String otpKey = OTP_KEY_PREFIX + phone;
            redisTemplate.opsForValue().set(otpKey, otp);
            redisTemplate.expire(otpKey, OTP_TTL.toSeconds(), TimeUnit.SECONDS);

            // Store registration payload temporarily
            String regKey = REGISTRATION_DATA_PREFIX + phone;
            String regValue = objectMapper.writeValueAsString(registrationRequest);
            redisTemplate.opsForValue().set(regKey, regValue);
            redisTemplate.expire(regKey, OTP_TTL.toSeconds(), TimeUnit.SECONDS);

            // Set resend lock
            redisTemplate.opsForValue().set(resendKey, "1");
            redisTemplate.expire(resendKey, RESEND_LOCK_TTL.toSeconds(), TimeUnit.SECONDS);

            log.info("✅ REGISTRATION_OTP - OTP process completed successfully");
            return Map.of(
                    "status", "true",
                    "message", "OTP sent successfully. Please use 1234 to verify.",
                    "expiresInMinutes", String.valueOf(OTP_TTL.toMinutes()),
                    "developmentMode", "true"
            );

        } catch (Exception e) {
            log.error("💥 REGISTRATION_OTP - Failed to send OTP: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send registration OTP: " + e.getMessage(), e);
        }
    }

    // ---------- Registration OTP verification ----------
    @Override
    public AuthResponse registerWithOtp(OTPVerificationRequest otpRequest) {
        String phone = otpRequest.getPhoneNumber();
        String providedOtp = otpRequest.getOtp();

        log.info("🔐 REGISTRATION_OTP_VERIFY - Starting OTP verification for phone: {}",
                maskPhoneNumber(phone));

        // Check for too many failed attempts
        if (isOTPAttemptsExceeded(phone)) {
            log.warn("🚫 REGISTRATION_OTP_VERIFY - Too many failed attempts for phone: {}", maskPhoneNumber(phone));
            throw new InvalidOTPException("Too many failed attempts. Please request a new OTP.");
        }

        String otpKey = OTP_KEY_PREFIX + phone;
        String storedOtp = redisTemplate.opsForValue().get(otpKey);

        log.info("🔍 REGISTRATION_OTP_VERIFY - Checking Redis key: {}, Found: {}",
                otpKey, storedOtp != null);

        if (storedOtp == null) {
            log.warn("⏰ REGISTRATION_OTP_VERIFY - OTP expired or not found for phone: {}", maskPhoneNumber(phone));
            throw new InvalidOTPException("OTP expired or not found. Please request a new OTP.");
        }

        if (!storedOtp.equals(providedOtp)) {
            incrementFailedOTPAttempts(phone);
            log.warn("❌ REGISTRATION_OTP_VERIFY - Invalid OTP provided. Expected: {}, Got: {} for phone: {}",
                    storedOtp, providedOtp, maskPhoneNumber(phone));
            throw new InvalidOTPException("Invalid OTP. Please try again.");
        }

        // OTP correct — remove OTP and load registration payload
        redisTemplate.delete(otpKey);
        clearFailedOTPAttempts(phone);

        String regKey = REGISTRATION_DATA_PREFIX + phone;
        String regJson = redisTemplate.opsForValue().get(regKey);
        if (regJson == null) {
            log.warn("⏰ REGISTRATION_OTP_VERIFY - Registration data expired for phone: {}", maskPhoneNumber(phone));
            throw new UserRegistrationException("Registration data expired. Please start the registration process again.");
        }

        try {
            RegistrationRequest registrationRequest = objectMapper.readValue(regJson, RegistrationRequest.class);
            // Delete registration cache as well
            redisTemplate.delete(regKey);

            log.info("✅ REGISTRATION_OTP_VERIFY - OTP verified successfully, proceeding with registration");
            // Proceed to create user
            return register(registrationRequest);
        } catch (Exception e) {
            log.error("💥 REGISTRATION_OTP_VERIFY - Failed to complete registration: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to complete registration: " + e.getMessage(), e);
        }
    }

    // ---------- Login OTP (send) ----------
    @Override
    public Map<String, String> sendLoginOTP(PhoneNumberRequest request) {
        String phone = request.getPhoneNumber();

        log.info("📱 LOGIN_OTP - OTP request for phone: {}", maskPhoneNumber(phone));

        // Validate phone number format
        if (!isValidPhoneNumber(phone)) {
            log.warn("📞 LOGIN_OTP - Invalid phone number format: {}", maskPhoneNumber(phone));
            return Map.of(
                    "status", "false",
                    "message", "Invalid phone number format"
            );
        }

        // Check if user exists
        if (!userRepository.existsByPhoneNumber(phone)) {
            log.warn("👤 LOGIN_OTP - User not found for phone: {}", maskPhoneNumber(phone));
            return Map.of(
                    "status", "false",
                    "message", "Phone number not registered. Please sign up first."
            );
        }

        // Check resend lock
        String resendKey = RESEND_LOCK_PREFIX + phone;
        Boolean hasLock = redisTemplate.hasKey(resendKey);
        if (Boolean.TRUE.equals(hasLock)) {
            Long ttl = redisTemplate.getExpire(resendKey, TimeUnit.SECONDS);
            log.warn("⏰ LOGIN_OTP - Resend lock active for {} seconds", ttl);
            return Map.of(
                    "status", "false",
                    "message", "Please wait " + ttl + " seconds before resending OTP"
            );
        }

        try {
            // Always use default OTP "1234" for development
            String otp = DEVELOPMENT_OTP;
            log.info("🧪 LOGIN_OTP - Using development OTP: {}", otp);

            // Store OTP in Redis with explicit TTL
            String otpKey = OTP_KEY_PREFIX + phone;
            redisTemplate.opsForValue().set(otpKey, otp);
            redisTemplate.expire(otpKey, OTP_TTL.toSeconds(), TimeUnit.SECONDS);

            log.info("💾 LOGIN_OTP - Stored OTP in Redis with key: {} for {} seconds",
                    otpKey, OTP_TTL.toSeconds());

            // Set resend lock
            redisTemplate.opsForValue().set(resendKey, "1");
            redisTemplate.expire(resendKey, RESEND_LOCK_TTL.toSeconds(), TimeUnit.SECONDS);

            log.info("✅ LOGIN_OTP - OTP process completed successfully");
            return Map.of(
                    "status", "true",
                    "message", "OTP sent successfully. Please use 1234 to verify.",
                    "expiresInMinutes", String.valueOf(OTP_TTL.toMinutes()),
                    "developmentMode", "true"
            );

        } catch (Exception e) {
            log.error("💥 LOGIN_OTP - Failed to send OTP: {}", e.getMessage(), e);
            return Map.of(
                    "status", "false",
                    "message", "Failed to send OTP. Please try again later."
            );
        }
    }

    // ---------- Login OTP verification ----------
    @Override
    public AuthResponse verifyLoginOTP(OTPVerificationRequest request) {
        String phone = request.getPhoneNumber();
        String providedOtp = request.getOtp();

        log.info("🔐 LOGIN_OTP_VERIFY - Starting OTP verification for phone: {}",
                maskPhoneNumber(phone));
        log.info("🔐 LOGIN_OTP_VERIFY - Provided OTP: {}", providedOtp);

        // Check for too many failed attempts
        if (isOTPAttemptsExceeded(phone)) {
            log.warn("🚫 LOGIN_OTP_VERIFY - Too many failed attempts for phone: {}", maskPhoneNumber(phone));
            throw new InvalidOTPException("Too many failed attempts. Please request a new OTP.");
        }

        String otpKey = OTP_KEY_PREFIX + phone;
        String storedOtp = redisTemplate.opsForValue().get(otpKey);

        log.info("🔍 LOGIN_OTP_VERIFY - Checking Redis key: {}, Found: {}, Stored OTP: {}",
                otpKey, storedOtp != null, storedOtp);

        if (storedOtp == null) {
            log.warn("⏰ LOGIN_OTP_VERIFY - OTP expired or not found for phone: {}", maskPhoneNumber(phone));
            throw new InvalidOTPException("OTP expired or not found. Please request a new OTP.");
        }

        if (!storedOtp.equals(providedOtp)) {
            incrementFailedOTPAttempts(phone);
            log.warn("❌ LOGIN_OTP_VERIFY - Invalid OTP provided. Expected: {}, Got: {} for phone: {}",
                    storedOtp, providedOtp, maskPhoneNumber(phone));
            throw new InvalidOTPException("Invalid OTP. Please try again.");
        }

        // OTP verified — consume it
        redisTemplate.delete(otpKey);
        clearFailedOTPAttempts(phone);

        // Authenticate user
        User user = userRepository.findByPhoneNumber(phone)
                .orElseThrow(() -> {
                    log.warn("👤 LOGIN_OTP_VERIFY - User not found for phone: {}", maskPhoneNumber(phone));
                    return new UserNotFoundException("User not found");
                });

        if (user.getIsActive() == null || user.getIsActive() != 1) {
            log.warn("🚫 LOGIN_OTP_VERIFY - Account inactive for user ID: {}", user.getId());
            throw new CustomAuthenticationException("Account inactive");
        }

        log.info("✅ LOGIN_OTP_VERIFY - Login successful for user ID: {}", user.getId());
        return authenticate(user);
    }

    // ---------- Rest of your existing methods unchanged ----------
    @Override
    public AuthResponse register(RegistrationRequest request) {
        log.info("📝 USER_REGISTRATION - Starting registration process for phone: {}",
                maskPhoneNumber(request.getPhoneNumber()));

        // Check for existing phone number
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            log.warn("📞 USER_REGISTRATION - Phone number already registered: {}",
                    maskPhoneNumber(request.getPhoneNumber()));
            throw new UserRegistrationException("Phone number already registered");
        }

        // Check for existing email
        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
            log.warn("📧 USER_REGISTRATION - Email already registered: {}", maskEmail(request.getEmail()));
            throw new UserRegistrationException("Email already registered");
        }

        // Handle null roleId properly using Integer wrapper class
        Integer roleId = request.getRoleId();
        if (roleId == null) {
            roleId = 3; // Default to USER role
            log.info("🔧 USER_REGISTRATION - Defaulting to USER role (3)");
        }
        log.info("👤 USER_REGISTRATION - Role ID: {}", roleId);

        // Store Manager must have password and storeId > 0
        if (roleId == 2) {
            log.info("🏪 USER_REGISTRATION - Registering store manager");

            if (request.getPassword() == null || request.getPassword().isEmpty()) {
                log.warn("🔑 USER_REGISTRATION - Password required for store manager");
                throw new UserRegistrationException("Password is required for store manager");
            }

            // Handle null storeId properly using Long wrapper class
            Long storeId = (long) request.getStoreId();
            if (storeId == null || storeId <= 0) {
                log.warn("🏪 USER_REGISTRATION - Valid store ID required for store manager. Provided: {}", storeId);
                throw new UserRegistrationException("Valid store must be assigned to store manager");
            }
            log.info("🏪 USER_REGISTRATION - Store manager assigned to store ID: {}", storeId);
        }

        try {
            // Map fields using mapper
            User user = userMapper.toUser(request);
            log.info("🔄 USER_REGISTRATION - User entity mapped from request");

            // Ensure roleId is set properly
            user.setRoleId(roleId);

            // Encode password only for store managers and admins
            if (roleId == 2) {
                user.setPassword(passwordEncoder.encode(request.getPassword()));
                log.info("🔑 USER_REGISTRATION - Password encoded for store manager");
            } else {
                user.setPassword(null); // For normal user, password not needed
                log.info("🔑 USER_REGISTRATION - No password set for regular user");
            }

            // Set timestamps and default values
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            user.setCreatedAt(timestamp);
            user.setUpdatedAt(timestamp);
            user.setIsActive(1);
            user.setIsDocumentVerified(0);

            log.info("⏰ USER_REGISTRATION - Timestamps and defaults set");

            // Save user to database
            User savedUser = userRepository.save(user);
            log.info("💾 USER_REGISTRATION - User saved to database with ID: {}", savedUser.getId());

            // Generate JWT token
            String token = jwtService.generateToken(savedUser);
            savedUser.setFirebaseToken(token);
            userRepository.save(savedUser);

            log.info("🔐 USER_REGISTRATION - JWT token generated and stored for user ID: {}", savedUser.getId());
            log.info("✅ USER_REGISTRATION - Registration completed successfully for user ID: {}", savedUser.getId());

            return new AuthResponse(token);

        } catch (Exception e) {
            log.error("💥 USER_REGISTRATION - Registration failed: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public AuthResponse registerAdmin(AdminRegistrationRequest request) {
        log.info("👑 ADMIN_REGISTRATION - Starting admin registration for email: {}", maskEmail(request.getEmail()));

        // Validate password confirmation
        if (!request.getPassword().equals(request.getPasswordConfirmation())) {
            log.warn("🔑 ADMIN_REGISTRATION - Passwords do not match");
            throw new UserRegistrationException("Passwords do not match");
        }

        // Check for existing email
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("📧 ADMIN_REGISTRATION - Email already registered: {}", maskEmail(request.getEmail()));
            throw new UserRegistrationException("Email already registered");
        }

        // Check for existing phone number (if provided)
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isEmpty()
                && userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            log.warn("📞 ADMIN_REGISTRATION - Phone number already registered: {}",
                    maskPhoneNumber(request.getPhoneNumber()));
            throw new UserRegistrationException("Phone number already registered");
        }

        try {
            User admin = userMapper.toAdmin(request);
            admin.setPassword(passwordEncoder.encode(request.getPassword()));

            Timestamp now = new Timestamp(System.currentTimeMillis());
            admin.setCreatedAt(now);
            admin.setUpdatedAt(now);
            admin.setIsActive(1);
            admin.setIsDocumentVerified(0);
            admin.setRoleId(1); // Ensure admin role is set

            User savedAdmin = userRepository.save(admin);
            log.info("💾 ADMIN_REGISTRATION - Admin saved to database with ID: {}", savedAdmin.getId());

            String token = jwtService.generateToken(savedAdmin);
            savedAdmin.setFirebaseToken(token);
            userRepository.save(savedAdmin);

            log.info("✅ ADMIN_REGISTRATION - Admin registration completed successfully for ID: {}", savedAdmin.getId());
            return new AuthResponse(token);

        } catch (Exception e) {
            log.error("💥 ADMIN_REGISTRATION - Registration failed: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public AuthResponse authenticate(User user) {
        log.info("🔐 USER_AUTHENTICATION - Authenticating user ID: {}", user.getId());

        // Use wrapper class Integer instead of primitive int comparison
        if (user == null || user.getIsActive() == null || user.getIsActive() != 1) {
            log.warn("🚫 USER_AUTHENTICATION - User not found or inactive. User ID: {}",
                    user != null ? user.getId() : "null");
            throw new CustomAuthenticationException("User not found or inactive");
        }

        try {
            String token = jwtService.generateToken(user);
            user.setFirebaseToken(token);
            userRepository.save(user);

            log.info("✅ USER_AUTHENTICATION - Authentication successful for user ID: {}", user.getId());
            return new AuthResponse(token);

        } catch (Exception e) {
            log.error("💥 USER_AUTHENTICATION - Authentication failed for user ID {}: {}",
                    user.getId(), e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public AuthResponse authenticate(String username, String password) {
        log.info("🔐 CREDENTIALS_AUTH - Authentication attempt for username: {}", maskUsername(username));

        User user = findByUsername(username);

        // Use wrapper class Integer instead of primitive int comparison
        if (user == null || user.getIsActive() == null || user.getIsActive() != 1) {
            log.warn("🚫 CREDENTIALS_AUTH - User not found or inactive for username: {}", maskUsername(username));
            throw new CustomAuthenticationException("User not found or inactive");
        }

        // Validate password if provided
        if (password != null && !password.isEmpty() &&
                (user.getPassword() == null || !passwordEncoder.matches(password, user.getPassword()))) {
            log.warn("🔑 CREDENTIALS_AUTH - Invalid credentials for username: {}", maskUsername(username));
            throw new CustomAuthenticationException("Invalid credentials");
        }

        try {
            String token = jwtService.generateToken(user);
            user.setFirebaseToken(token);
            userRepository.save(user);

            log.info("✅ CREDENTIALS_AUTH - Authentication successful for user ID: {}", user.getId());
            return new AuthResponse(token);

        } catch (Exception e) {
            log.error("💥 CREDENTIALS_AUTH - Authentication failed for username {}: {}",
                    maskUsername(username), e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserByPhoneNumber(String phoneNumber) {
        log.info("🔍 USER_LOOKUP - Finding user by phone: {}", maskPhoneNumber(phoneNumber));

        return userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> {
                    log.warn("❌ USER_LOOKUP - User not found for phone: {}", maskPhoneNumber(phoneNumber));
                    return new UserNotFoundException("User not found");
                });
    }

    @Override
    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        log.debug("🔍 USERNAME_LOOKUP - Finding user by username: {}", maskUsername(username));

        // Try email first
        Optional<User> userByEmail = userRepository.findByEmail(username);
        if (userByEmail.isPresent() && userByEmail.get().getIsActive() != null && userByEmail.get().getIsActive() == 1) {
            log.debug("✅ USERNAME_LOOKUP - Found active user by email");
            return userByEmail.get();
        }

        // Try phone number
        Optional<User> userByPhone = userRepository.findByPhoneNumber(username);
        if (userByPhone.isPresent() && userByPhone.get().getIsActive() != null && userByPhone.get().getIsActive() == 1) {
            log.debug("✅ USERNAME_LOOKUP - Found active user by phone");
            return userByPhone.get();
        }

        log.debug("❌ USERNAME_LOOKUP - No active user found for username: {}", maskUsername(username));
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmailAndActive(String email, int isActive) {
        log.debug("🔍 EMAIL_LOOKUP - Finding user by email: {} with status: {}", maskEmail(email), isActive);

        // Use wrapper class Integer instead of primitive int comparison
        return userRepository.findByEmail(email)
                .filter(u -> u.getIsActive() != null && u.getIsActive() == isActive);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("🔐 USER_DETAILS - Loading user details for username: {}", maskUsername(username));

        User user = findByUsername(username);
        if (user == null) {
            log.warn("❌ USER_DETAILS - User not found for username: {}", maskUsername(username));
            throw new UsernameNotFoundException("User not found: " + username);
        }

        log.info("✅ USER_DETAILS - User details loaded for user ID: {}", user.getId());
        return user;
    }

    @Override
    public Page<User> getAllStoreManagers(Pageable pageable) {
        log.info("📊 STORE_MANAGERS - Fetching store managers with pagination: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<User> managers = userRepository.findByRoleId(2, pageable);
        log.info("✅ STORE_MANAGERS - Found {} store managers", managers.getTotalElements());

        return managers;
    }

    @Override
    public Page<User> getAllUsers(Pageable pageable) {
        log.info("📊 USERS - Fetching regular users with pagination: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<User> users = userRepository.findByRoleId(3, pageable);
        log.info("✅ USERS - Found {} regular users", users.getTotalElements());

        return users;
    }

    // ---------- Helper Methods ----------
    private boolean isDevelopmentMode() {
        return "development".equalsIgnoreCase(environment) || developmentOTPBypass;
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false;
        }
        // Basic phone number validation - adjust regex as per your requirements
        return phoneNumber.matches("^[+]?[0-9]{10,15}$");
    }

    private void incrementFailedOTPAttempts(String phoneNumber) {
        String failedKey = FAILED_ATTEMPTS_PREFIX + phoneNumber;
        String attempts = redisTemplate.opsForValue().get(failedKey);
        int attemptCount = attempts != null ? Integer.parseInt(attempts) : 0;
        attemptCount++;

        redisTemplate.opsForValue().set(failedKey, String.valueOf(attemptCount));
        redisTemplate.expire(failedKey, FAILED_ATTEMPTS_TTL.toSeconds(), TimeUnit.SECONDS);
        log.warn("🔢 OTP_ATTEMPTS - Failed attempt #{} for phone: {}", attemptCount, maskPhoneNumber(phoneNumber));
    }

    private boolean isOTPAttemptsExceeded(String phoneNumber) {
        String failedKey = FAILED_ATTEMPTS_PREFIX + phoneNumber;
        String attempts = redisTemplate.opsForValue().get(failedKey);
        int attemptCount = attempts != null ? Integer.parseInt(attempts) : 0;
        return attemptCount >= MAX_OTP_ATTEMPTS;
    }

    private void clearFailedOTPAttempts(String phoneNumber) {
        String failedKey = FAILED_ATTEMPTS_PREFIX + phoneNumber;
        redisTemplate.delete(failedKey);
        log.info("🧹 OTP_ATTEMPTS - Cleared failed attempts for phone: {}", maskPhoneNumber(phoneNumber));
    }

    // Helper methods for safe logging
    private String maskPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() < 4) {
            return "";
        }
        return phoneNumber.substring(0, 2) + "" + phoneNumber.substring(phoneNumber.length() - 2);
    }

    private String maskEmail(String email) {
        if (email == null || email.isEmpty()) {
            return "";
        }
        if (email.contains("@")) {
            String[] parts = email.split("@");
            return parts[0].substring(0, Math.min(2, parts[0].length())) + "*@" + parts[1];
        }
        return "";
    }

    private String maskUsername(String username) {
        if (username == null || username.isEmpty()) {
            return "";
        }
        if (username.contains("@")) {
            return maskEmail(username);
        }
        return maskPhoneNumber(username);
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileDTO getUserProfile(String token) {
        try {
            log.info("📋 GET_USER_PROFILE - Fetching profile using JWT token");

            // ✅ CRITICAL FIX: Extract USER ID from JWT token, not phone number
            Long userId = jwtService.extractUserId(token);

            if (userId == null) {
                log.error("💥 GET_USER_PROFILE - User ID not found in JWT token");
                throw new UserNotFoundException("Invalid token: User ID not found");
            }

            log.info("🔍 GET_USER_PROFILE - Extracted user ID from token: {}", userId);

            // ✅ Look up user by ID (not phone number!)
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> {
                        log.warn("👤 GET_USER_PROFILE - User not found for ID: {}", userId);
                        return new UserNotFoundException("User not found with ID: " + userId);
                    });

            // ✅ Check if user is active
            if (user.getIsActive() == null || user.getIsActive() != 1) {
                log.warn("🚫 GET_USER_PROFILE - User account is inactive: {}", user.getId());
                throw new CustomAuthenticationException("Account is inactive");
            }

            log.info("✅ GET_USER_PROFILE - Profile retrieved successfully for user ID: {}", user.getId());

            // ✅ FIXED: Use correct mapper method name
            return userMapper.toUserProfileDTO(user);

        } catch (UserNotFoundException | CustomAuthenticationException e) {
            throw e;
        } catch (Exception e) {
            log.error("💥 GET_USER_PROFILE - Failed to get user profile: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve user profile: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileDTO getUserProfileById(Long userId) {
        log.info("👤 GET_USER_PROFILE_BY_ID - Getting user profile for ID: {}", userId);

        // ✅ FIXED: Removed Math.toIntExact - use Long directly
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("👤 GET_USER_PROFILE_BY_ID - User not found for ID: {}", userId);
                    return new UserNotFoundException("User not found with ID: " + userId);
                });

        // Check if user is active
        if (user.getIsActive() == null || user.getIsActive() != 1) {
            log.warn("🚫 GET_USER_PROFILE_BY_ID - User is inactive: {}", userId);
            throw new CustomAuthenticationException("User account is inactive");
        }

        log.info("✅ GET_USER_PROFILE_BY_ID - User profile retrieved successfully for ID: {}", userId);

        // ✅ FIXED: Use correct mapper method name
        return userMapper.toUserProfileDTO(user);
    }



    @Override
    @Transactional
    public UserProfileDTO updateUserProfile(Long userId, UserProfileUpdateRequest request) {
        log.info("📝 UPDATE_USER_PROFILE - Updating profile for user ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("👤 UPDATE_USER_PROFILE - User not found for ID: {}", userId);
                    return new UserNotFoundException("User not found with ID: " + userId);
                });

        // Check if user is active
        if (user.getIsActive() == null || user.getIsActive() != 1) {
            log.warn("🚫 UPDATE_USER_PROFILE - User is inactive: {}", userId);
            throw new CustomAuthenticationException("User account is inactive");
        }

        try {
            // Validate specific fields before mapping
            if (request.getName() != null && request.getName().trim().isEmpty()) {
                log.warn("📝 UPDATE_USER_PROFILE - Empty name provided for user ID: {}", userId);
                throw new IllegalArgumentException("Name cannot be empty");
            }

            if (request.getIfsc() != null && !request.getIfsc().trim().isEmpty()) {
                String ifsc = request.getIfsc().trim().toUpperCase();
                if (!ifsc.matches("^[A-Z]{4}0[A-Z0-9]{6}$")) {
                    log.warn("🏦 UPDATE_USER_PROFILE - Invalid IFSC format: {}", ifsc);
                    throw new IllegalArgumentException("Invalid IFSC code format");
                }
                request.setIfsc(ifsc);
            }

            // Handle profile image update
            MultipartFile newProfileImage = request.getProfileImage();
            if (newProfileImage != null && !newProfileImage.isEmpty()) {
                // Delete old image if exists
                String oldImagePath = user.getProfile();
                if (oldImagePath != null && !oldImagePath.trim().isEmpty()) {
                    boolean deleted = imageUtils.deleteImage(oldImagePath);
                    if (deleted) {
                        log.info("🗑️ UPDATE_USER_PROFILE - Deleted old profile image: {}", oldImagePath);
                    } else {
                        log.warn("⚠️ UPDATE_USER_PROFILE - Failed to delete old profile image: {}", oldImagePath);
                    }
                }

                // Save new image
                String newImagePath = imageUtils.saveProfileImage(newProfileImage, userId);
                user.setProfile(newImagePath);
                log.info("📸 UPDATE_USER_PROFILE - Saved new profile image: {}", newImagePath);
            }

            log.info("🔄 UPDATE_USER_PROFILE - Using mapper to update user entity from request");
            // Map other fields ignoring nulls (excluding profile image handled above)
            userMapper.updateUserFromProfileUpdateRequest(request, user);

            // Update timestamp
            user.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

            // Save updated user
            User savedUser = userRepository.save(user);
            log.info("✅ UPDATE_USER_PROFILE - Profile updated successfully for user ID: {}", userId);

            return userMapper.toUserProfileDTO(savedUser);

        } catch (IOException e) {
            log.error("Image upload failed", e);
            throw new RuntimeException("Image upload failed: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    // Add method to get user ID from token (if not already in JwtService)
    public Long getUserIdFromToken(String token) {
        try {
            // Remove "Bearer " prefix if present
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            // Extract user ID from token (assuming your JWT contains user ID)
            return jwtService.extractUserId(token);
        } catch (Exception e) {
            log.error("💥 GET_USER_ID_FROM_TOKEN - Failed to extract user ID: {}", e.getMessage());
            throw new CustomAuthenticationException("Invalid token");
        }
    }

    @Override
    public User adminRegisterUser(SimpleUserDto dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setAlternateNumber(dto.getAlternateNumber());
        user.setEmail(dto.getEmail());
        user.setRoleId(3); // Role user
        user.setIsActive(1);
        return userRepository.save(user);
    }


    @Override
    public List<UserProfileDTO> searchUsers(String searchText) {
        List<User> users;
        if (searchText == null || searchText.trim().isEmpty()) {
            users = userRepository.findByRoleId(3);
        } else {
            users = userRepository.searchUsersByText(searchText.trim());
        }
        return users.stream()
                .map(userMapper::toUserProfileDTO)
                .collect(Collectors.toList());
    }

}