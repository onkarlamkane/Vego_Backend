package com.eptiq.vegobike.dtos;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
@Getter
@Setter
public class
PhoneNumberRequest {
    @NotBlank
    private String phoneNumber;
}
