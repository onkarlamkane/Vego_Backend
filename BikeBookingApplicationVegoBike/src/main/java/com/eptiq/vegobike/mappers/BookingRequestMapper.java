package com.eptiq.vegobike.mappers;

import com.eptiq.vegobike.dtos.*;
import com.eptiq.vegobike.model.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface BookingRequestMapper {
    BookingRequestDto toDto(BookingRequest entity);
    BookingRequest toEntity(BookingRequestDto dto);

    BookingBikeResponse toBookingResponseDto(BookingRequest entity);

    @Mapping(target = "bikeDetails", source = "bike")
    @Mapping(target = "id", source = "entity.id")
    @Mapping(target = "createdAt", source = "entity.createdAt")
    @Mapping(target = "updatedAt", source = "entity.updatedAt")
    BookingBikeResponse toBookingResponseDto(BookingRequest entity, Bike bike);

    @Mapping(target = "isPuc", source = "isPuc", qualifiedByName = "intToBoolean")
    @Mapping(target = "isInsurance", source = "isInsurance", qualifiedByName = "intToBoolean")
    @Mapping(target = "isDocuments", source = "isDocuments", qualifiedByName = "intToBoolean")
    BikeResponseDTO toBikeResponseDTO(Bike bike);

    @Named("intToBoolean")
    static boolean intToBoolean(int value) {
        return value != 0;
    }
}
