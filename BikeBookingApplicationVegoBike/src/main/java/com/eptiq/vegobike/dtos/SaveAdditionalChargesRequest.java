package com.eptiq.vegobike.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveAdditionalChargesRequest {
    private Integer bookingId;
    private List<String> chargesType;
    private List<BigDecimal> chargesAmount;
}