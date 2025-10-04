package com.eptiq.vegobike.mappers;

import com.eptiq.vegobike.dtos.VehicleTypeDto;
import com.eptiq.vegobike.model.VehicleType;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VehicleTypeMapper {
    VehicleTypeDto toDto(VehicleType entity);
    VehicleType toEntity(VehicleTypeDto dto);
    List<VehicleTypeDto> toDtoList(List<VehicleType> entities);
}
