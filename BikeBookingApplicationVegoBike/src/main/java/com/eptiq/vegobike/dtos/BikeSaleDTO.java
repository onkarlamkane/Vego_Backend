package com.eptiq.vegobike.dtos;

import lombok.*;
import java.sql.Timestamp;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BikeSaleDTO {
    private Long id;
    private String nameOfPerson;
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
    private String bikeCondition;
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
    private String status;
    private String isRepairRequired;
    private Double sellingPrice;
    private Double sellingClosingPrice;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String addedBy;
    private Long sellId;
    private Timestamp deletedAt;
}
