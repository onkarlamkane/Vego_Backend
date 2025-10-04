package com.eptiq.vegobike.mappers;

import com.eptiq.vegobike.dtos.BikeSellImageDTO;
import com.eptiq.vegobike.model.BikeSellImage;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BikeSellImageMapper {
    BikeSellImageDTO toDTO(BikeSellImage bikeSellImage);
    BikeSellImage toEntity(BikeSellImageDTO bikeSellImageDTO);
}
