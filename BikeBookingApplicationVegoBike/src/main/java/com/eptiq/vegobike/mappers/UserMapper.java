package com.eptiq.vegobike.mappers;

import com.eptiq.vegobike.dtos.AdminRegistrationRequest;
import com.eptiq.vegobike.dtos.RegistrationRequest;
import com.eptiq.vegobike.dtos.UserProfileDTO;
import com.eptiq.vegobike.dtos.UserProfileUpdateRequest;
import com.eptiq.vegobike.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.BeanMapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // Registration mappings
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roleId", expression = "java(request.getRoleId())")
    @Mapping(target = "storeId", expression = "java(request.getStoreId())")
    @Mapping(target = "isActive", constant = "1")
    @Mapping(target = "isDocumentVerified", constant = "0")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "firebaseToken", ignore = true)
    @Mapping(target = "rememberToken", ignore = true)
    @Mapping(target = "otp", ignore = true)
    User toUser(RegistrationRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roleId", constant = "1") // Admin
    @Mapping(target = "isActive", constant = "1")
    @Mapping(target = "isDocumentVerified", constant = "0")
    @Mapping(target = "storeId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "firebaseToken", ignore = true)
    @Mapping(target = "rememberToken", ignore = true)
    @Mapping(target = "otp", ignore = true)
    User toAdmin(AdminRegistrationRequest request);

    // Profile mappings - User entity to UserProfileDTO
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "phoneNumber", source = "phoneNumber")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "alternateNumber", source = "alternateNumber")
    @Mapping(target = "address", source = "address")
    @Mapping(target = "accountNumber", source = "accountNumber")
    @Mapping(target = "ifsc", source = "ifsc")
    @Mapping(target = "upiId", source = "upiId")
    @Mapping(target = "profile", source = "profile")
    UserProfileDTO toUserProfileDTO(User user);

    // Update user entity from UserProfileUpdateRequest
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "phoneNumber", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "alternateNumber", ignore = true)
    @Mapping(target = "roleId", ignore = true)
    @Mapping(target = "storeId", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "isDocumentVerified", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "firebaseToken", ignore = true)
    @Mapping(target = "rememberToken", ignore = true)
    @Mapping(target = "otp", ignore = true)
    void updateUserFromProfileUpdateRequest(UserProfileUpdateRequest request, @MappingTarget User user);
}