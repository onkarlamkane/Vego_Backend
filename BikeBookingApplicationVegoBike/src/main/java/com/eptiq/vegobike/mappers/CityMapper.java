package com.eptiq.vegobike.mappers;

import com.eptiq.vegobike.dtos.CityDto;
import com.eptiq.vegobike.model.City;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CityMapper {

    CityDto toDto(City entity);

    City toEntity(CityDto dto);

    List<CityDto> toDtoList(List<City> entities);

}