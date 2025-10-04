package com.eptiq.vegobike.mappers;

import com.eptiq.vegobike.dtos.BikeServiceDto;
import com.eptiq.vegobike.model.BikeService;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BikeServiceMapper {

    // ✅ Entity → DTO
    @Mapping(target = "serviceType", expression = "java(entity.getServiceType())")
    @Mapping(target = "status", expression = "java(entity.getStatus())")
    BikeServiceDto toDto(BikeService entity);

    // ✅ DTO → Entity
    @Mapping(target = "serviceTypeCode", expression = "java(dto.getServiceType() != null ? dto.getServiceType().getCode() : null)")
    @Mapping(target = "statusCode", expression = "java(\"ACTIVE\".equalsIgnoreCase(dto.getStatus()) ? 1 : (\"INACTIVE\".equalsIgnoreCase(dto.getStatus()) ? 0 : null))")
    BikeService toEntity(BikeServiceDto dto);

    // ✅ List<Entity> → List<DTO>
    List<BikeServiceDto> toDtoList(List<BikeService> entities);

    // ✅ Update existing entity from DTO (used for updates)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "serviceTypeCode", expression = "java(dto.getServiceType() != null ? dto.getServiceType().getCode() : entity.getServiceTypeCode())")
    @Mapping(target = "statusCode", expression = "java(dto.getStatus() != null ? (\"ACTIVE\".equalsIgnoreCase(dto.getStatus()) ? 1 : (\"INACTIVE\".equalsIgnoreCase(dto.getStatus()) ? 0 : entity.getStatusCode())) : entity.getStatusCode())")
    void updateEntityFromDto(BikeServiceDto dto, @MappingTarget BikeService entity);
}
