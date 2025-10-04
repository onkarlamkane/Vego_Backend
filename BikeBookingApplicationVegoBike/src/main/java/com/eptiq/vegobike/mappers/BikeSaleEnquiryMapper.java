package com.eptiq.vegobike.mappers;

import com.eptiq.vegobike.dtos.BikeSaleEnquiryDTO;
import com.eptiq.vegobike.model.BikeSaleEnquiry;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public interface BikeSaleEnquiryMapper {

    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "formatTimestamp")
    @Mapping(target = "updatedAt", source = "updatedAt", qualifiedByName = "formatTimestamp")
    BikeSaleEnquiryDTO toDTO(BikeSaleEnquiry entity);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    BikeSaleEnquiry toEntity(BikeSaleEnquiryDTO dto);

    @Named("formatTimestamp")
    default String formatTimestamp(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return timestamp.toLocalDateTime().format(formatter);
    }
}

