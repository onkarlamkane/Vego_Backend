package com.eptiq.vegobike.dtos;


import lombok.Data;

@Data
public class AdminRegisterAndBookRequest {
    private SimpleUserDto customer;
    private BookingRequestDto booking;
}
