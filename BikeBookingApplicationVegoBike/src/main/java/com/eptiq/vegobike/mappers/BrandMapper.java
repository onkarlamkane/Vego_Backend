package com.eptiq.vegobike.mappers;

import com.eptiq.vegobike.dtos.BrandDTO;
import com.eptiq.vegobike.model.Brand;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BrandMapper {

    BrandDTO toDTO(Brand entity);

    Brand toEntity(BrandDTO dto);

    List<BrandDTO> toDTOList(List<Brand> entities);

}