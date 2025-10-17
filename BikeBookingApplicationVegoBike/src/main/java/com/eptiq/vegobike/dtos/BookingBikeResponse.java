package com.eptiq.vegobike.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingBikeResponse {
    private Integer id;
    private String bookingId;
    private Integer vehicleId;
    private Long customerId;
    private String customerName;
    private String customerNumber;
    private Date startDate;
    private Date endDate;
    private Date startDate1;
    private Date endDate1;
    private Float charges;
    private Float additionalCharges;
    private String additionalChargesDetails;
    private Float additionalHours;
    private Float advanceAmount;
    private Float totalHours;
    private Float gst;
    private Double km;
    private Float deliveryCharges;
    private Float totalCharges;
    private Float finalAmount;
    private Integer pickupLocationId;
    private Integer dropLocationId;
    private Integer bookingStatus;
    private String status; // e.g. "Confirmed"
    private Integer paymentType;
    private String addressType;
    private String address;
    private String transactionId;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Integer couponId;
    private String couponCode;
    private Float couponAmount;
    private String deliveryType;
    private Integer lateFeeCharges;
    private Timestamp lateEndDate;
    private String merchantTransactionId;
    private String paymentStatus;
    private String formatedStartDate;
    private String formatedEndDate;
    private String createdDate;
    private String razorpayOrderDetails;
    private BikeResponseDTO bikeDetails; // Nested DTO
    private String message;
    private Double startTripKm;
    private Double endTripKm;
    private List<String> startTripImages;
    private List<String> endTripImages;

}