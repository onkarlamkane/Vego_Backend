package com.eptiq.vegobike.mappers;

import com.eptiq.vegobike.dtos.ServiceOrderDTO;
import com.eptiq.vegobike.model.ServiceOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ServiceOrderMapper {
    ServiceOrderMapper INSTANCE = Mappers.getMapper(ServiceOrderMapper.class);

    ServiceOrderDTO toDto(ServiceOrder entity);


    @Mapping(target = "serviceAddressType", source = "serviceAddressType")
    ServiceOrder toEntity(ServiceOrderDTO dto);

    ServiceOrderDTO toDTO(ServiceOrder entity);
}
