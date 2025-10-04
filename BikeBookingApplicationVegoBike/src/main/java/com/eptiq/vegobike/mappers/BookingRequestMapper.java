package com.eptiq.vegobike.mappers;

import com.eptiq.vegobike.dtos.*;
import com.eptiq.vegobike.model.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface BookingRequestMapper {
    BookingRequestDto toDto(BookingRequest entity);
    BookingRequest toEntity(BookingRequestDto dto);

    BookingBikeResponse toBookingResponseDto(BookingRequest entity);
}
