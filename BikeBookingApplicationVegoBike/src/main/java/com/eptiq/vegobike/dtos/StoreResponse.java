package com.eptiq.vegobike.dtos;

import lombok.*;
import java.sql.Timestamp;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class StoreResponse {

    private Integer id;
    private Integer addedBy;
    private String storeName;
    private String storeContactNumber;
    private String storeGstinNumber;
    private String storeAddress;
    private String storeUrl;
    private String storeImage;
    private Double storeLatitude;
    private Double storeLongitude;
    private Integer isActive;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Integer cityId;
    private String cityName;
    private Boolean doorstepAvailable;
}
