package com.eptiq.vegobike.dtos;

import lombok.Data;

@Data
public class SimpleUserDto {
    private String name;
    private String phoneNumber;
    private String alternateNumber;
    private String email;
}
