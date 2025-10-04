package com.eptiq.vegobike.dtos;
import com.eptiq.vegobike.enums.ChargeType;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class LateChargeResponseDTO {
    private Integer id;
    private Integer categoryId;
    private ChargeType chargeType;
    private Float charge;
    private Integer isActive;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}