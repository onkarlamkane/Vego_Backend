package com.eptiq.vegobike.services.impl;

import com.eptiq.vegobike.dtos.OfferDto;
import com.eptiq.vegobike.mappers.OfferMapper;
import com.eptiq.vegobike.model.Offer;
import com.eptiq.vegobike.repositories.BookingRequestRepository;
import com.eptiq.vegobike.repositories.OfferRepository;
import com.eptiq.vegobike.services.OfferService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class OfferServiceImpl implements OfferService {


    private OfferRepository offerRepository;
    private BookingRequestRepository bookingRequestRepository;


    private OfferMapper offerMapper;

    @Override
    public List<OfferDto> getAllOffers() {
        return offerRepository.findAll().stream()
                .map(offerMapper::toDto) // INSTANCE method reference
                .collect(Collectors.toList());
    }

    @Override
    public OfferDto getOfferById(Integer id) {
        return offerRepository.findById(id)
                .map(offerMapper::toDto)
                .orElse(null);
    }

    @Override
    public OfferDto createOffer(OfferDto offerDto) {
        Offer offer = offerMapper.toEntity(offerDto);
        Offer saved = offerRepository.save(offer);
        return offerMapper.toDto(saved);
    }

    @Override
    public OfferDto updateOffer(Integer id, OfferDto offerDto) {
        Offer offer = offerMapper.toEntity(offerDto);
        offer.setId(id); // Make sure Offer entity has setId method
        Offer updated = offerRepository.save(offer);
        return offerMapper.toDto(updated);
    }

    @Override
    public void toggleOfferStatus(Integer offerId) {
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new RuntimeException("Offer not found"));
        int currentStatus = offer.getIsActive() != null ? offer.getIsActive() : 1;
        offer.setIsActive(currentStatus == 1 ? 0 : 1);
        offerRepository.save(offer);
    }

    @Override
    public List<OfferDto> getActiveOffers() {
        return offerMapper.toDtoList(offerRepository.findByIsActive(1));
    }

    public Float applyOfferForUser(Integer customerId, Integer vehicleId, String couponCode, Float originalPrice) {
        Offer offer = offerRepository.findByOfferCode(couponCode)
                .orElseThrow(() -> new RuntimeException("Offer code not found."));

        // Check if offer is active
        if (offer.getIsActive() == null || offer.getIsActive() == 0) {
            throw new RuntimeException("Offer is not active.");
        }
        // Check if coupons are available
        if (offer.getRemainingCoupon() == null || offer.getRemainingCoupon() <= 0) {
            throw new RuntimeException("This offer is no longer available.");
        }
        // Minimum ride amount
        if (originalPrice < offer.getMinimumAmount()) {
            throw new RuntimeException("Minimum ride amount must be ₹" + offer.getMinimumAmount());
        }

        // --- FIRST ORDER LOGIC ---
        if ("first".equalsIgnoreCase(offer.getAppliesTo())) {
            boolean alreadyUsed = bookingRequestRepository.existsByCustomerIdAndCouponId(customerId, offer.getId());
            if (alreadyUsed) {
                throw new RuntimeException("Offer already used.");
            }
        } else if ("entire".equalsIgnoreCase(offer.getAppliesTo())) {
            boolean alreadyUsed = bookingRequestRepository.existsByCustomerIdAndCouponId(customerId, offer.getId());
            if (offer.getUsageLimit() != null && offer.getUsageLimit() == 1 && alreadyUsed) {
                throw new RuntimeException("Offer already used.");
            }
        }

        // Start/end date validation
        Date today = new Date();
        if (offer.getStartDate() != null && today.before(offer.getStartDate())) {
            throw new RuntimeException("Offer not valid yet. Valid from: " + offer.getStartDate());
        }
        if (offer.getEndDate() != null && today.after(offer.getEndDate())) {
            throw new RuntimeException("Offer expired on: " + offer.getEndDate());
        }

        // Discount calculation
        Float discount = null;
        if ("amount".equalsIgnoreCase(offer.getDiscountType())) {
            discount = offer.getDiscountValue().floatValue();
        } else if ("percentage".equalsIgnoreCase(offer.getDiscountType())) {
            discount = (originalPrice * offer.getDiscountValue()) / 100f;
        }
        Float finalPrice = Math.max(originalPrice - discount, 0f);

        // DO NOT update offer quantity here!
        return finalPrice;
    }



}
