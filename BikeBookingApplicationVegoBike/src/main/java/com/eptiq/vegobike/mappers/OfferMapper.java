package com.eptiq.vegobike.mappers;

import com.eptiq.vegobike.dtos.OfferDto;
import com.eptiq.vegobike.model.Offer;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OfferMapper {
    OfferDto toDto(Offer offer);
    Offer toEntity(OfferDto dto);
    List<OfferDto> toDtoList(List<Offer> offers);
}
