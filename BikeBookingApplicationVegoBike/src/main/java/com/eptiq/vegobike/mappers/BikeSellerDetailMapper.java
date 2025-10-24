package com.eptiq.vegobike.mappers;

import com.eptiq.vegobike.dtos.BikeSellerDetailDTO;
import com.eptiq.vegobike.model.BikeSellerDetail;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BikeSellerDetailMapper {
    BikeSellerDetail toEntity(BikeSellerDetailDTO dto);
    BikeSellerDetailDTO toDTO(BikeSellerDetail entity);
}

