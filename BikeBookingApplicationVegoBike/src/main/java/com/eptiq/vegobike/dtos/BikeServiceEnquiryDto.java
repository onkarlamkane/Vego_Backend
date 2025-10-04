package com.eptiq.vegobike.dtos;

import lombok.Data;
import java.sql.Timestamp;

@Data
public class BikeServiceEnquiryDto {
    private long id;
    private int brandId;
    private Timestamp createdAt;
    private int customerId;
    private int modelId;
    private int quantity;
    private int serviceId;
    private String serviceType;
    private int status;
    private int storeId;
    private Timestamp updatedAt;
}
