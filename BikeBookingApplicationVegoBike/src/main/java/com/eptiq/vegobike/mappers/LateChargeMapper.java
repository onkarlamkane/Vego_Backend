package com.eptiq.vegobike.mappers;

import com.eptiq.vegobike.dtos.LateChargeRequestDTO;
import com.eptiq.vegobike.dtos.LateChargeResponseDTO;
import com.eptiq.vegobike.model.LateCharge;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface LateChargeMapper {
    LateChargeMapper INSTANCE = Mappers.getMapper(LateChargeMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", constant = "1")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    LateCharge toEntity(LateChargeRequestDTO dto);

    LateChargeResponseDTO toResponse(LateCharge entity);
}