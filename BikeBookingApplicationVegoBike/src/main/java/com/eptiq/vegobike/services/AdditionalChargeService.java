package com.eptiq.vegobike.services;

import com.eptiq.vegobike.dtos.AdditionalChargeDto;
import com.eptiq.vegobike.dtos.SaveAdditionalChargesRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface AdditionalChargeService {
    Map<String, Object> saveAdditionalCharges(SaveAdditionalChargesRequest request);
    Map<String, Object> removeCharge(Long chargeId);
    List<AdditionalChargeDto> getChargesByBookingId(Integer bookingId);
    BigDecimal calculateTotalAdditionalCharges(Integer bookingId);
}
