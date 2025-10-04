package com.eptiq.vegobike.mappers;

import com.eptiq.vegobike.dtos.*;
import com.eptiq.vegobike.model.Model;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ModelMapper {

    @Mapping(target = "brandName", source = "brand.brandName")
    ModelResponse toResponse(Model entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", constant = "1")
    @Mapping(target = "brand", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(source = "brandId", target = "brandId")
    Model toEntity(ModelCreateRequest req);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "brand", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(ModelUpdateRequest req, @MappingTarget Model entity);
}
