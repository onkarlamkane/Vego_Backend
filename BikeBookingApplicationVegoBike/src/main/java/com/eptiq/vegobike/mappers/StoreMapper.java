package com.eptiq.vegobike.mappers;

import com.eptiq.vegobike.dtos.*;
import com.eptiq.vegobike.model.City;
import com.eptiq.vegobike.model.Store;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface StoreMapper {

    StoreResponse toResponse(Store entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", constant = "1")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "storeImage", ignore = true) // Set separately in service
    @Mapping(target = "city", source = "cityId")
    Store toEntity(StoreCreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "storeImage", ignore = true) // Set separately in service
    @Mapping(target = "city", source = "cityId")
    void updateEntityFromDto(StoreUpdateRequest request, @MappingTarget Store entity);


    default City mapCity(Integer cityId) {
        if(cityId == null) return null;
        City city = new City();
        city.setId(cityId);
        return city;
    }

}
