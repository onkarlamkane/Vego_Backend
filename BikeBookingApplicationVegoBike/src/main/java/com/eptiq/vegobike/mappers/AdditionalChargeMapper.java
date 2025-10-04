package com.eptiq.vegobike.mappers;

import com.eptiq.vegobike.dtos.AdditionalChargeDto;
import com.eptiq.vegobike.model.AdditionalCharge;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AdditionalChargeMapper {

    AdditionalChargeDto toDto(AdditionalCharge entity);

    AdditionalCharge toEntity(AdditionalChargeDto dto);
}
