package com.eptiq.vegobike.mappers;

import com.eptiq.vegobike.dtos.PriceListDTO;
import com.eptiq.vegobike.model.PriceList;

public interface PriceListMapper {

    static PriceList toEntity(PriceListDTO dto) {
        if (dto == null) return null;

        return PriceList.builder()
                .id(dto.getId())
                .categoryId(dto.getCategoryId())
                .days(dto.getDays())
                .deposit(dto.getDeposit())
                .hourlyChargeAmount(dto.getHourlyChargeAmount())
                .price(dto.getPrice())
                .isActive(dto.getIsActive() != null ? dto.getIsActive() : 1)
                .build();
    }

    static PriceListDTO toDto(PriceList entity) {
        if (entity == null) return null;

        return PriceListDTO.builder()
                .id(entity.getId())
                .categoryId(entity.getCategoryId())
                .days(entity.getDays())
                .deposit(entity.getDeposit())
                .hourlyChargeAmount(entity.getHourlyChargeAmount())
                .price(entity.getPrice())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    static void updateEntityFromDto(PriceListDTO dto, PriceList entity) {
        if (dto == null || entity == null) return;

        if (dto.getCategoryId() != null) entity.setCategoryId(dto.getCategoryId());
        if (dto.getDays() != null) entity.setDays(dto.getDays());
        if (dto.getDeposit() != null) entity.setDeposit(dto.getDeposit());
        if (dto.getHourlyChargeAmount() != null) entity.setHourlyChargeAmount(dto.getHourlyChargeAmount());
        if (dto.getPrice() != null) entity.setPrice(dto.getPrice());
        if (dto.getIsActive() != null) entity.setIsActive(dto.getIsActive());
    }
}
