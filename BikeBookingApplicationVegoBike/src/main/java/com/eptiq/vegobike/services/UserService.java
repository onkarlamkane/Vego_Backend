package com.eptiq.vegobike.services;

import com.eptiq.vegobike.dtos.*;
import com.eptiq.vegobike.exceptions.UserNotFoundException;
import com.eptiq.vegobike.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Optional;

/**
 * Enhanced User Service Interface for VegoBike Authentication System
 *
 * Provides comprehensive user management including:
 * - OTP-based registration and login for mobile users
 * - Password-based authentication for admin and store managers
 * - User lookup and management operations
 * - Role-based user operations with proper security
 *
 * Extends Spring Security's UserDetailsService for seamless integration
 *
 * @author VegoBike Team
 * @version 2.0
 * @since 2025-09-27
 */
public interface UserService extends UserDetailsService {


    Map<String, String> sendRegistrationOTP(String requestJson, MultipartFile profileImage);

    AuthResponse registerWithOtp(OTPVerificationRequest otpRequest);


    Map<String, String> sendLoginOTP(PhoneNumberRequest request);

    AuthResponse verifyLoginOTP(OTPVerificationRequest request);


    AuthResponse register(RegistrationRequest request);

    AuthResponse authenticate(User user);

    AuthResponse authenticate(String username, String password);


    AuthResponse registerAdmin(AdminRegistrationRequest request);

    default AuthResponse registerStoreManager(StoreManagerRegistrationRequest request) {
        // Default implementation - can be overridden in implementation class
        throw new UnsupportedOperationException("Store manager registration not implemented");
    }


    User getUserByPhoneNumber(String phoneNumber);

    User findByUsername(String username);

    default Optional<User> getUserById(Long userId) {
        // Default implementation - should be overridden in implementation
        throw new UnsupportedOperationException("getUserById not implemented");
    }

    default Optional<User> getUserByEmail(String email) {
        // Default implementation - should be overridden in implementation
        throw new UnsupportedOperationException("getUserByEmail not implemented");
    }

    Optional<User> findByEmailAndActive(String email, int isActive);


    Page<User> getAllStoreManagers(Pageable pageable);

    Page<User> getAllUsers(Pageable pageable);

    default Page<User> getAllAdmins(Pageable pageable) {
        // Default implementation - should be overridden
        throw new UnsupportedOperationException("getAllAdmins not implemented");
    }

    default Page<User> getUsersByRole(Integer roleId, Pageable pageable) {
        return switch (roleId) {
            case 1 -> getAllAdmins(pageable);
            case 2 -> getAllStoreManagers(pageable);
            case 3 -> getAllUsers(pageable);
            default -> throw new IllegalArgumentException("Invalid role ID: " + roleId);
        };
    }

    default Page<User> getUsersByStore(Long storeId, Pageable pageable) {
        // Default implementation - should be overridden
        throw new UnsupportedOperationException("getUsersByStore not implemented");
    }


    default User activateUser(Long userId) {
        throw new UnsupportedOperationException("activateUser not implemented");
    }

    default User deactivateUser(Long userId) {
        throw new UnsupportedOperationException("deactivateUser not implemented");
    }



    default User verifyUserDocuments(Long userId) {
        throw new UnsupportedOperationException("verifyUserDocuments not implemented");
    }


    default Page<User> searchUsers(String searchTerm, Pageable pageable) {
        throw new UnsupportedOperationException("searchUsers not implemented");
    }

    default Page<User> getUsersWithFilters(Map<String, Object> filters, Pageable pageable) {
        throw new UnsupportedOperationException("getUsersWithFilters not implemented");
    }


    default Map<String, Long> getUserStatistics() {
        throw new UnsupportedOperationException("getUserStatistics not implemented");
    }

    default Map<Integer, Long> getUserCountByRole() {
        throw new UnsupportedOperationException("getUserCountByRole not implemented");
    }

    default Page<User> getRecentlyRegisteredUsers(int days, Pageable pageable) {
        throw new UnsupportedOperationException("getRecentlyRegisteredUsers not implemented");
    }

    default boolean isPhoneNumberExists(String phoneNumber) {
        try {
            getUserByPhoneNumber(phoneNumber);
            return true;
        } catch (UserNotFoundException e) {
            return false;
        }
    }

    default boolean isEmailExists(String email) {
        return getUserByEmail(email).isPresent();
    }

    default boolean isUserActive(Long userId) {
        return getUserById(userId)
                .map(user -> user.getIsActive() != null && user.getIsActive() == 1)
                .orElse(false);
    }

    default boolean isUserDocumentVerified(Long userId) {
        return getUserById(userId)
                .map(user -> user.getIsDocumentVerified() != null && user.getIsDocumentVerified() == 1)
                .orElse(false);
    }


    default boolean isAdmin(Long userId) {
        return getUserById(userId)
                .map(user -> user.getRoleId() != null && user.getRoleId() == 1)
                .orElse(false);
    }

    default boolean isStoreManager(Long userId) {
        return getUserById(userId)
                .map(user -> user.getRoleId() != null && user.getRoleId() == 2)
                .orElse(false);
    }


    default boolean isRegularUser(Long userId) {
        return getUserById(userId)
                .map(user -> user.getRoleId() == null || user.getRoleId() == 3)
                .orElse(false);
    }

    UserProfileDTO getUserProfile(String token);
    UserProfileDTO getUserProfileById(Long userId);
    UserProfileDTO updateUserProfile(Long userId, UserProfileUpdateRequest request);
    Long getUserIdFromToken(String token);

    User adminRegisterUser(SimpleUserDto dto);

}
