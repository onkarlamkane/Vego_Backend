package com.eptiq.vegobike.dtos;

import com.eptiq.vegobike.enums.ServiceAddressType;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class ServiceOrderDTO {


    private Long id;
    private int customerId;
    private int storeId; // used if STORE
    private ServiceAddressType serviceAddressType;
    private String doorstepAddress; // used if DOORSTEP
    private String vehicleNumber;
    private String chasisNumber;
    private String engineNumber;
    private double orderAmount;
    private String finalAmount;
    private int discountType;
    private int percent;
    private int orderStatus;
    private String paymentMethod;
    private String paymentStatus;
    private String slotTime;
    private String serviceComments;
    private String nextServiceDate;


}
