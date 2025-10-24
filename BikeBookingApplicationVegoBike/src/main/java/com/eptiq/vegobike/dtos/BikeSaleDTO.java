package com.eptiq.vegobike.dtos;

import com.eptiq.vegobike.enums.BikeCondition;
import com.eptiq.vegobike.enums.ListingStatus;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BikeSaleDTO {
    private Long id;
    private String name;
    private String email;
    private String contactNumber;
    private String alternateContactNumber;
    private String city;
    private String pincode;
    private String address;
    private Long categoryId;
    private Long brandId;
    private Long modelId;
    private Long yearId;
    private String color;
    private String registrationNumber;
    private String vehicleChassisNumber;
    private String vehicleEngineNumber;
    private Integer numberOfOwner;
    private Integer kmsDriven;
    private Double price;

    private BikeCondition bikeCondition;

    private String bikeDescription;
    private Long storeId;
    private String pucImage;
    private String insuranceImage;
    private String documentImage;
    private Boolean isPuc;
    private Boolean isInsurance;
    private Boolean isDocument;
    private Double customerSellingClosingPrice;
    private String supervisorName;
    private String inspectionBikeCondition;
    private String additionalNotes;
    private ListingStatus listingStatus;
    //private String status;
    private Boolean isRepairRequired;

    private BigDecimal sellingPrice;
    private BigDecimal sellingClosingPrice;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String addedBy;
    private Long sellId;
    private Timestamp deletedAt;

    // Images
    private MultipartFile front_image;
    private MultipartFile back_image;
    private MultipartFile left_image;
    private MultipartFile right_image;
    private MultipartFile puc_image;
    private MultipartFile insurance_image;
    private MultipartFile document_image;
}
