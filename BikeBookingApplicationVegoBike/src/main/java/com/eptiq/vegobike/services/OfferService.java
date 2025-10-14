package com.eptiq.vegobike.services;

import com.eptiq.vegobike.dtos.OfferDto;

import java.util.List;

public interface OfferService {
    List<OfferDto> getAllOffers();
    OfferDto getOfferById(Integer id);
    OfferDto createOffer(OfferDto offerDto);
    OfferDto updateOffer(Integer id, OfferDto offerDto);
    void toggleOfferStatus(Integer offerId);
    List<OfferDto> getActiveOffers();
    public Float applyOfferForUser(Integer customerId, Integer vehicleId, String couponCode, Float originalPrice);



}