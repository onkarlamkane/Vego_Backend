package com.eptiq.vegobike.dtos;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class StoreUpdateRequest {

    private Integer addedBy;

    @Size(max = 255, message = "Store name must be less than 255 characters")
    private String storeName;

    @Pattern(regexp = "^[0-9]{10,15}$", message = "Contact number must be 10-15 digits")
    private String storeContactNumber;

    private String storeGstinNumber;

    @Size(max = 1000, message = "Address must be less than 1000 characters")
    private String storeAddress;

    private String storeUrl;

    private MultipartFile storeImage;

    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    private Double storeLatitude;

    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    private Double storeLongitude;

    private Integer cityId;
}
