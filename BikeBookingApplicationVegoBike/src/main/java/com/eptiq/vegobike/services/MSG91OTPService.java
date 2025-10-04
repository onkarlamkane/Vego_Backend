package com.eptiq.vegobike.services;


public interface MSG91OTPService {
    void sendOTP(String phoneNumber);
    boolean verifyOTP(String phoneNumber, String otp);
}
