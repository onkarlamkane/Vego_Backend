package com.eptiq.vegobike.mappers;

import com.eptiq.vegobike.dtos.ServiceOrderItemDTO;
import com.eptiq.vegobike.model.ServiceOrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ServiceOrderItemMapper {
    ServiceOrderItemMapper INSTANCE = Mappers.getMapper(ServiceOrderItemMapper.class);

    ServiceOrderItemDTO toDto(ServiceOrderItem entity);
    ServiceOrderItem toEntity(ServiceOrderItemDTO dto);
}
