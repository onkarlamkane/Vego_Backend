package com.eptiq.vegobike.dtos;

import lombok.Data;

@Data
public class ServiceOrderItemDTO {
    private Long id;
    private Long serviceId;
    private String serviceName;
    private Double servicePrice;
    private Integer quantity;
}
