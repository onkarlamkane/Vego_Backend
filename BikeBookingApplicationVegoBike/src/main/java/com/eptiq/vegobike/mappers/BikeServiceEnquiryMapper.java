package com.eptiq.vegobike.mappers;

import com.eptiq.vegobike.dtos.BikeServiceEnquiryDto;
import com.eptiq.vegobike.model.BikeServiceEnquiry;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")  //
public interface BikeServiceEnquiryMapper {

    BikeServiceEnquiryMapper INSTANCE = Mappers.getMapper(BikeServiceEnquiryMapper.class);

    BikeServiceEnquiryDto toDto(BikeServiceEnquiry entity);

    BikeServiceEnquiry toEntity(BikeServiceEnquiryDto dto);
}
