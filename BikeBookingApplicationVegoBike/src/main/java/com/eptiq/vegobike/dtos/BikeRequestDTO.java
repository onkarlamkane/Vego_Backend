package com.eptiq.vegobike.dtos;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequestDto {
    //    private int id;
//    private String bookingId;
    private Integer customerId;
    private int vehicleId;
    private Date startDate;
    private Date endDate;
    private float charges;
    private float additionalCharges;
    private String additionalChargesDetails;
    private float advanceAmount;
    private float finalAmount;
    private float gst;
    private float totalHours;
    //    private int bookingStatus;
//    private String paymentStatus;
    private String addressType;
    private String address;
//    private Timestamp createdAt;
//    private Timestamp updatedAt;

    // Additional fields that match BookingRequest entity
    private float additionalHours;
    private float couponAmount;
    private String couponCode;
    private int couponId;
    private float deliveryCharges;
    private String deliveryType;
    private int dropLocationId;
    private Date endDate1;
    private double km;
    private Timestamp lateEndDate;
    private int lateFeeCharges;
    // private String merchantTransactionId;
    private int paymentType;
    private int pickupLocationId;
    private Date startDate1;
    private float totalCharges;
    //  private String transactionId;

    // UI display fields
    private String statusName; // "Confirmed", "Accepted", etc.
    private String customerName;
    private String vehicleRegistrationNumber;
}