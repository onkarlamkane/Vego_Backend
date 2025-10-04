package com.eptiq.vegobike.mappers;

import com.eptiq.vegobike.dtos.*;
import com.eptiq.vegobike.model.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface BookingStatusMapper {
    BookingStatusDto toDto(BookingStatus entity);
    BookingStatus toEntity(BookingStatusDto dto);
}
