package com.eptiq.vegobike.mappers;

import com.eptiq.vegobike.dtos.BikeSellImageDTO;
import com.eptiq.vegobike.model.BikeSellImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BikeSellImageMapper {
    @Mapping(source = "frontImages", target = "frontImages")
    @Mapping(source = "backImages", target = "backImages")
    @Mapping(source = "leftImages", target = "leftImages")
    @Mapping(source = "rightImages", target = "rightImages")
    BikeSellImageDTO toDTO(BikeSellImage bikeSellImage);
    BikeSellImage toEntity(BikeSellImageDTO bikeSellImageDTO);
}
