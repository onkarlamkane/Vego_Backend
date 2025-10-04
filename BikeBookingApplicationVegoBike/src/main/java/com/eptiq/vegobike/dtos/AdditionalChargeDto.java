package com.eptiq.vegobike.dtos;

import lombok.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdditionalChargeDto {
    private Long id;
    private BigInteger bookingRequestId;
    private String chargeType;
    private BigDecimal amount;
}
