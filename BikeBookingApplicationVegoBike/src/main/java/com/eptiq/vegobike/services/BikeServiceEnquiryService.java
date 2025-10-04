package com.eptiq.vegobike.services;

import com.eptiq.vegobike.dtos.BikeServiceEnquiryDto;
import java.util.List;

public interface BikeServiceEnquiryService {
    BikeServiceEnquiryDto addToCart(BikeServiceEnquiryDto dto);
    List<BikeServiceEnquiryDto> getCartByCustomer(Long customerId);
    void removeFromCart(Long id);
    List<BikeServiceEnquiryDto> getAll();
}
