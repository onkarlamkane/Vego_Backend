package com.eptiq.vegobike.mappers;

import com.eptiq.vegobike.dtos.AvailableBikeDto;
import com.eptiq.vegobike.dtos.BikeDocumentsDTO;
import com.eptiq.vegobike.dtos.BikeRequestDTO;
import com.eptiq.vegobike.dtos.BikeResponseDTO;
import com.eptiq.vegobike.model.Bike;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BikeMapper {



    // Map Bike entity to BikeResponseDTO with images provided separately
    static BikeResponseDTO toDTO(Bike bike, String brandName,
                                 String categoryName,
                                 String modelName,
                                 String status, List<String> images) {
        return BikeResponseDTO.builder()
                .id(bike.getId())
                .name(bike.getName())
                .registrationNumber(bike.getRegistrationNumber())
                .chassisNumber(bike.getChassisNumber())
                .engineNumber(bike.getEngineNumber())
                .brandId(bike.getBrandId())
                .brandName(brandName)
                .categoryId(bike.getCategoryId())
                .categoryName(categoryName)
                .modelId(bike.getModelId())
                .modelName(modelName)
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
                .isActive(bike.getIsActive() == 1)
                .status(status)
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

    // Custom mapping helper: int → boolean
    @Named("intToBoolean")
    default boolean intToBoolean(int value) {
        return value == 1;
    }

    // Custom mapping helper: boolean → int (if you need reverse mapping)
    @Named("booleanToInt")
    default int booleanToInt(boolean value) {
        return value ? 1 : 0;
    }
}
