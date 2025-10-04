package com.eptiq.vegobike.dtos;

import com.eptiq.vegobike.enums.ServiceType;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BikeServiceDto {
    private Long id;
    private Integer brandId;
    private Integer categoryId;
    private Integer modelId;
    private Integer yearId;
    private String serviceName;
    private String serviceDescription;
    private String serviceImage;
    private ServiceType serviceType;  // exposed as enum
    private String status;            // exposed as string
    private BigDecimal price;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
