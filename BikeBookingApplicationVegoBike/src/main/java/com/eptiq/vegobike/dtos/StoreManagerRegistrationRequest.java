package com.eptiq.vegobike.dtos;

import lombok.Data;

@Data
public class StoreManagerRegistrationRequest {
    private String name;
    private String email;
    private String phoneNumber;
    private String password;
    private String passwordConfirmation;
    private Long storeId;
}
