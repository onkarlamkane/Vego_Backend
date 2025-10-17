package com.eptiq.vegobike.model;

import java.io.Serializable;

import jakarta.persistence.*;

import java.util.Date;
import java.sql.Timestamp;


/**
 * The persistent class for the booking_requests database table.
 */
@Entity
@Table(name = "booking_requests")
@NamedQuery(name = "BookingRequest.findAll", query = "SELECT b FROM BookingRequest b")
public class BookingRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "additional_charges")
    private Float additionalCharges;

    @Lob
    @Column(name = "additional_charges_details")
    private String additionalChargesDetails;

    @Column(name = "additional_hours")
    private Float additionalHours;

    @Lob
    private String address;

    @Column(name = "address_type")
    private String addressType;

    @Column(name = "advance_amount")
    private Float advanceAmount;

    @Column(name = "booking_id")
    private String bookingId;

    @Column(name = "booking_status")
    private Integer bookingStatus;

    private Float charges;

    @Column(name = "coupon_amount")
    private Float couponAmount;

    @Column(name = "coupon_code")
    private String couponCode;

    @Column(name = "coupon_id")
    private Integer couponId;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "delivery_charges")
    private Float deliveryCharges;

    @Column(name = "delivery_type")
    private String deliveryType;

    @Column(name = "drop_location_id")
    private Integer dropLocationId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_date")
    private Date endDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "end_date1")
    private Date endDate1;

    @Column(name = "final_amount")
    private Float finalAmount;

    private Float gst;

    private Double km;

    @Column(name = "late_end_date")
    private Timestamp lateEndDate;

    @Column(name = "late_fee_charges")
    private Integer lateFeeCharges;

    @Lob
    @Column(name = "merchant_transaction_id")
    private String merchantTransactionId;

    @Lob
    @Column(name = "payment_status")
    private String paymentStatus;

    @Column(name = "payment_type")
    private Integer paymentType;

    @Column(name = "pickup_location_id")
    private Integer pickupLocationId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_date")
    private Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "start_date1")
    private Date startDate1;

    @Column(name = "total_charges")
    private Float totalCharges;

    @Column(name = "total_hours")
    private Float totalHours;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "vehicle_id")
    private Integer vehicleId;


    @Column(name = "start_trip_km")
    private Double startTripKm; // value entered at trip start

    @Column(name = "end_trip_km")
    private Double endTripKm;   // value entered at trip end



    public BookingRequest() {
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Float getAdditionalCharges() {
        return this.additionalCharges;
    }

    public void setAdditionalCharges(Float additionalCharges) {
        this.additionalCharges = additionalCharges;
    }

    public String getAdditionalChargesDetails() {
        return this.additionalChargesDetails;
    }

    public void setAdditionalChargesDetails(String additionalChargesDetails) {
        this.additionalChargesDetails = additionalChargesDetails;
    }

    public Float getAdditionalHours() {
        return this.additionalHours;
    }

    public void setAdditionalHours(Float additionalHours) {
        this.additionalHours = additionalHours;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddressType() {
        return this.addressType;
    }

    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }

    public Float getAdvanceAmount() {
        return this.advanceAmount;
    }

    public void setAdvanceAmount(Float advanceAmount) {
        this.advanceAmount = advanceAmount;
    }

    public String getBookingId() {
        return this.bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public Integer getBookingStatus() {
        return this.bookingStatus;
    }

    public void setBookingStatus(int bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public Float getCharges() {
        return this.charges;
    }

    public void setCharges(Float charges) {
        this.charges = charges;
    }

    public Float getCouponAmount() {
        return this.couponAmount;
    }

    public void setCouponAmount(Float couponAmount) {
        this.couponAmount = couponAmount;
    }

    public String getCouponCode() {
        return this.couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public Integer getCouponId() {
        return this.couponId;
    }

    public void setCouponId(int couponId) {
        this.couponId = couponId;
    }

    public Timestamp getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Long getCustomerId() {
        return this.customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Float getDeliveryCharges() {
        return this.deliveryCharges;
    }

    public void setDeliveryCharges(Float deliveryCharges) {
        this.deliveryCharges = deliveryCharges;
    }

    public String getDeliveryType() {
        return this.deliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    public Integer getDropLocationId() {
        return this.dropLocationId;
    }

    public void setDropLocationId(int dropLocationId) {
        this.dropLocationId = dropLocationId;
    }

    public Date getEndDate() {
        return this.endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getEndDate1() {
        return this.endDate1;
    }

    public void setEndDate1(Date endDate1) {
        this.endDate1 = endDate1;
    }

    public Float getFinalAmount() {
        return this.finalAmount;
    }

    public void setFinalAmount(Float finalAmount) {
        this.finalAmount = finalAmount;
    }

    public Float getGst() {
        return this.gst;
    }

    public void setGst(Float gst) {
        this.gst = gst;
    }

    public Double getKm() {
        return this.km;
    }

    public void setKm(Double km) {
        this.km = km;
    }

    public Timestamp getLateEndDate() {
        return this.lateEndDate;
    }

    public void setLateEndDate(Timestamp lateEndDate) {
        this.lateEndDate = lateEndDate;
    }

    public Integer getLateFeeCharges() {
        return this.lateFeeCharges;
    }

    public void setLateFeeCharges(int lateFeeCharges) {
        this.lateFeeCharges = lateFeeCharges;
    }

    public String getMerchantTransactionId() {
        return this.merchantTransactionId;
    }

    public void setMerchantTransactionId(String merchantTransactionId) {
        this.merchantTransactionId = merchantTransactionId;
    }

    public String getPaymentStatus() {
        return this.paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Integer getPaymentType() {
        return this.paymentType;
    }

    public void setPaymentType(int paymentType) {
        this.paymentType = paymentType;
    }

    public Integer getPickupLocationId() {
        return this.pickupLocationId;
    }

    public void setPickupLocationId(int pickupLocationId) {
        this.pickupLocationId = pickupLocationId;
    }

    public Date getStartDate() {
        return this.startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getStartDate1() {
        return this.startDate1;
    }

    public void setStartDate1(Date startDate1) {
        this.startDate1 = startDate1;
    }

    public Float getTotalCharges() {
        return this.totalCharges;
    }

    public void setTotalCharges(Float totalCharges) {
        this.totalCharges = totalCharges;
    }

    public Float getTotalHours() {
        return this.totalHours;
    }

    public void setTotalHours(Float totalHours) {
        this.totalHours = totalHours;
    }

    public String getTransactionId() {
        return this.transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Timestamp getUpdatedAt() {
        return this.updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getVehicleId() {
        return this.vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public void generateBookingId() {
        if(this.bookingId == null || this.bookingId.isEmpty()){
            this.bookingId = String.format("VEGO%03d", this.id);
        }

    }

    public Double getStartTripKm() {
        return startTripKm;
    }

    public void setStartTripKm(Double startTripKm) {
        this.startTripKm = startTripKm;
    }

    public Double getEndTripKm() {
        return endTripKm;
    }

    public void setEndTripKm(Double endTripKm) {
        this.endTripKm = endTripKm;
    }


}