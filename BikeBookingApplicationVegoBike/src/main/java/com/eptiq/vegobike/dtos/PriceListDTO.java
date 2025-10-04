package com.eptiq.vegobike.dtos;

import lombok.*;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceListDTO {

    private Long id;

    @NotNull(message = "Category ID is required")
    @Positive(message = "Category ID must be positive")
    private Integer categoryId;

    @NotNull(message = "Days is required")
    @Min(value = 0, message = "Days must be 0 (hourly) or positive")
    @Max(value = 365, message = "Days cannot exceed 365")
    private Integer days;

    @NotNull(message = "Deposit amount is required")
    @DecimalMin(value = "0.0", message = "Deposit must be non-negative")
    @Digits(integer = 8, fraction = 2, message = "Invalid deposit format")
    private BigDecimal deposit;

    @DecimalMin(value = "0.0", message = "Hourly charge must be non-negative")
    @Digits(integer = 8, fraction = 2, message = "Invalid hourly charge format")
    private BigDecimal hourlyChargeAmount;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Invalid price format")
    private BigDecimal price;

    private Integer isActive;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // ✅ Helper methods
    public String getDaysDisplay() {
        if (days == null) return "Unknown";
        return days == 0 ? "Hourly" : days + " Day" + (days > 1 ? "s" : "");
    }

    public boolean isHourlyRate() {
        return days != null && days == 0;
    }
}
