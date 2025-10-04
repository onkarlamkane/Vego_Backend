package com.eptiq.vegobike.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BikeSaleEnquiryDTO {
    private Long id;
    private Integer bikeId;
    private String createdAt; // String format for frontend
    private Integer customerId;
    private String enquiryId;
    private String status;
    private String updatedAt; // String format for frontend
}
