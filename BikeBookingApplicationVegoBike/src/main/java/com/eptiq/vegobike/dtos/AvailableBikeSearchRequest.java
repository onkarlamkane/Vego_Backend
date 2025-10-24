package com.eptiq.vegobike.dtos;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvailableBikeSearchRequest {
    private Integer cityId;       // City where the booking is needed
    private Long storeId;      // Store to filter available bikes
    private Date startDate;       // Desired booking/pickup date and time
    private Date endDate;         // Desired drop-off date and time
    private String searchText;    // (Optional) Search string for bike model, registration, etc.
}
