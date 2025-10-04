package com.eptiq.vegobike.dtos;

import com.eptiq.vegobike.enums.ChargeType;
import lombok.Data;

@Data
public class LateChargeRequestDTO {
    private Integer categoryId;
    private ChargeType chargeType;
    private Float charge;
}