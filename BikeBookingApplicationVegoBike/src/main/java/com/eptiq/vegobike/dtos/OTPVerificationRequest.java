package com.eptiq.vegobike.dtos;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
@Getter
@Setter
public class OTPVerificationRequest {
    @NotBlank
    private String phoneNumber;

    @NotBlank
    @Size(min = 4, max = 4)
    private String otp;
}
