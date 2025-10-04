//package com.eptiq.vegobike.dtos;
//
//import lombok.Data;
//import java.sql.Timestamp;
//
//@Data
//public class AvailableBikeDto {
//    private int id;
//    private String name;                // Bike name
//    private String registrationNumber;  // registration_number in table
//    private String brand;               // If you'd like to show brand
//    private String model;               // Optional: model name
//    private String addressType;         // e.g., "Self Pickup"/"Delivery at location"
//    private int isActive;
//    private Timestamp createdAt;
//    private Timestamp updatedAt;
//    // You can add any other fields you want to expose
//}


package com.eptiq.vegobike.dtos;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvailableBikeDto {
    private int id;
    private String name;
    private int categoryId;
    private Integer modelId;
    private String registrationNumber;
    private String storeName;
    private String mainImageUrl;
    private List<PriceListDTO> packages;

    // In AvailableBikeDto
    public AvailableBikeDto(int id, String name, int categoryId, Integer modelId,
                            String registrationNumber, String storeName, String mainImageUrl) {
        this.id = id;
        this.name = name;
        this.categoryId = categoryId;
        this.modelId = modelId;
        this.registrationNumber = registrationNumber;
        this.storeName = storeName;
        this.mainImageUrl = mainImageUrl;
    }


}