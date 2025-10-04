package com.eptiq.vegobike.mappers;

import com.eptiq.vegobike.dtos.AvailableBikeDto;
import com.eptiq.vegobike.dtos.BikeDocumentsDTO;
import com.eptiq.vegobike.dtos.BikeRequestDTO;
import com.eptiq.vegobike.dtos.BikeResponseDTO;
import com.eptiq.vegobike.model.Bike;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BikeMapper {

    // Map Bike entity to BikeResponseDTO with images provided separately
    static BikeResponseDTO toDTO(Bike bike, List<String> images) {
        return BikeResponseDTO.builder()
                .id(bike.getId())
                .name(bike.getName())
                .registrationNumber(bike.getRegistrationNumber())
                .chassisNumber(bike.getChassisNumber())
                .engineNumber(bike.getEngineNumber())
                .brandId(bike.getBrandId())
                .categoryId(bike.getCategoryId())
                .modelId(bike.getModelId())
                .registrationYearId(bike.getRegistrationYearId())
                .storeId(bike.getStoreId())
                .price(bike.getPrice())
                .isPuc(bike.getIsPuc() == 1)
                .isInsurance(bike.getIsInsurance() == 1)
                .isDocuments(bike.getIsDocuments() == 1)
                .pucImageUrl(bike.getPucImage())
                .insuranceImageUrl(bike.getInsuranceImage())
                .documentImageUrl(bike.getDocumentImage())
                .bikeImages(images)
                .build();
    }

    // Map Bike entity to AvailableBikeDto (use for available bikes list)
    AvailableBikeDto toAvailableBikeDto(Bike bike);

    List<AvailableBikeDto> toAvailableBikeDtoList(List<Bike> bikes);

    // Convert request DTO to Bike entity for creation
    @Mapping(target = "pucImage", ignore = true)
    @Mapping(target = "insuranceImage", ignore = true)
    @Mapping(target = "documentImage", ignore = true)
    Bike toEntity(BikeRequestDTO request);

    // Update existing Bike entity from request DTO for updates
    @Mapping(target = "pucImage", ignore = true)
    @Mapping(target = "insuranceImage", ignore = true)
    @Mapping(target = "documentImage", ignore = true)
    void updateEntity(BikeRequestDTO request, @MappingTarget Bike bike);


    @Mapping(source = "documentImage", target = "documentImage")
    @Mapping(source = "insuranceImage", target = "insuranceImage")
    @Mapping(source = "pucImage", target = "pucImage")
    BikeDocumentsDTO toBikeDocumentsDto(Bike bike);
}
