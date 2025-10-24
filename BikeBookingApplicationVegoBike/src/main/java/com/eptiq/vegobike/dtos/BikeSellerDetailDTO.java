package com.eptiq.vegobike.dtos;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BikeSellerDetailDTO {
    private Long id;
    private Long bikeId;
    private String name;
    private String contactNumber;
    private String alternateContactNumber;
    private String email;
    private String address;
    private String city;
    private String pincode;
}
