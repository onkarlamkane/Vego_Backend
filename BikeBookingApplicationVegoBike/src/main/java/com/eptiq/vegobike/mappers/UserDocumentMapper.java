package com.eptiq.vegobike.mappers;

import com.eptiq.vegobike.dtos.UserDocumentDTO;
import com.eptiq.vegobike.model.UserDocument;
import com.eptiq.vegobike.enums.VerificationStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserDocumentMapper {

    @Mapping(target = "adhaarFrontStatus", expression = "java(com.eptiq.vegobike.enums.VerificationStatus.fromCode(doc.getIsAdhaarFrontVerified()))")
    @Mapping(target = "adhaarBackStatus", expression = "java(com.eptiq.vegobike.enums.VerificationStatus.fromCode(doc.getIsAdhaarBackVerified()))")
    @Mapping(target = "licenseStatus", expression = "java(com.eptiq.vegobike.enums.VerificationStatus.fromCode(doc.getIsLicenseVerified()))")
    @Mapping(target = "active", expression = "java(doc.getIsActive() == 1)")
    UserDocumentDTO toDTO(UserDocument doc);

    @Mapping(target = "isAdhaarFrontVerified", expression = "java(dto.getAdhaarFrontStatus() != null ? dto.getAdhaarFrontStatus().getCode() : 0)")
    @Mapping(target = "isAdhaarBackVerified", expression = "java(dto.getAdhaarBackStatus() != null ? dto.getAdhaarBackStatus().getCode() : 0)")
    @Mapping(target = "isLicenseVerified", expression = "java(dto.getLicenseStatus() != null ? dto.getLicenseStatus().getCode() : 0)")
    @Mapping(target = "isActive", expression = "java(dto.getActive() != null && dto.getActive() ? 1 : 0)")
    UserDocument toEntity(UserDocumentDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDTO(UserDocumentDTO dto, @MappingTarget UserDocument entity);
}
