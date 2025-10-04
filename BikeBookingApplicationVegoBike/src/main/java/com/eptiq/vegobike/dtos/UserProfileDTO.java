package com.eptiq.vegobike.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserProfileDTO {
    private Long id;
    private String name;
    private String phoneNumber;
    private String email;
    private String alternateNumber;
    private String address;
    private String accountNumber;
    private String ifsc;
    private String upiId;
    private String profile; // Profile image URL
}