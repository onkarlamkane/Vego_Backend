package com.eptiq.vegobike.services.impl;

import com.eptiq.vegobike.dtos.AdditionalChargeDto;
import com.eptiq.vegobike.dtos.SaveAdditionalChargesRequest;
import com.eptiq.vegobike.exceptions.ResourceNotFoundException;
import com.eptiq.vegobike.mappers.AdditionalChargeMapper;
import com.eptiq.vegobike.model.AdditionalCharge;
import com.eptiq.vegobike.model.BookingRequest;
import com.eptiq.vegobike.repositories.AdditionalChargeRepository;
import com.eptiq.vegobike.repositories.BookingRequestRepository;
import com.eptiq.vegobike.services.AdditionalChargeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdditionalChargeServiceImpl implements AdditionalChargeService {

    private final AdditionalChargeRepository additionalChargeRepository;
    private final BookingRequestRepository bookingRequestRepository;
    private final AdditionalChargeMapper mapper;

    @Override
    @Transactional
    public Map<String, Object> saveAdditionalCharges(SaveAdditionalChargesRequest request) {
        try {
            log.info("💰 SAVE_CHARGES - Saving additional charges for booking: {}", request.getBookingId());

            // Validate booking exists
            BookingRequest booking = bookingRequestRepository.findById(request.getBookingId())
                    .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + request.getBookingId()));

            // Validate arrays are same size
            if (request.getChargesType().size() != request.getChargesAmount().size()) {
                log.error("❌ SAVE_CHARGES - Charge types and amounts size mismatch");
                throw new IllegalArgumentException("Charge types and amounts must match");
            }

            BigInteger bookingRequestId = BigInteger.valueOf(request.getBookingId());

            // Delete existing charges for this booking
            List<AdditionalCharge> existingCharges = additionalChargeRepository.findByBookingRequestId(bookingRequestId);
            if (!existingCharges.isEmpty()) {
                log.info("🗑️ SAVE_CHARGES - Deleting {} existing charges", existingCharges.size());
                additionalChargeRepository.deleteAll(existingCharges);
            }

            // Save new charges
            BigDecimal totalAdditionalCharges = BigDecimal.ZERO;
            int savedCount = 0;

            for (int i = 0; i < request.getChargesType().size(); i++) {
                BigDecimal amount = request.getChargesAmount().get(i);
                if (amount != null && amount.compareTo(BigDecimal.ZERO) > 0) {
                    AdditionalCharge charge = new AdditionalCharge();
                    charge.setBookingRequestId(bookingRequestId);
                    charge.setChargeType(request.getChargesType().get(i));
                    charge.setAmount(amount);
                    charge.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                    charge.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

                    additionalChargeRepository.save(charge);
                    totalAdditionalCharges = totalAdditionalCharges.add(amount);
                    savedCount++;

                    log.debug("💰 SAVE_CHARGES - Saved charge: {} - Amount: {}",
                            request.getChargesType().get(i), amount);
                }
            }

            // Update booking with total additional charges
            booking.setAdditionalCharges(totalAdditionalCharges.floatValue());
            booking.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            bookingRequestRepository.save(booking);

            log.info("✅ SAVE_CHARGES - Successfully saved {} charges totaling {} for booking: {}",
                    savedCount, totalAdditionalCharges, request.getBookingId());

            return Map.of(
                    "status", "success",
                    "message", "Charges saved successfully",
                    "total", totalAdditionalCharges,
                    "count", savedCount
            );

        } catch (ResourceNotFoundException e) {
            log.error("❌ SAVE_CHARGES - Booking not found: {}", request.getBookingId());
            throw e;
        } catch (IllegalArgumentException e) {
            log.error("❌ SAVE_CHARGES - Invalid request: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("💥 SAVE_CHARGES - Error saving charges: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save additional charges: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public Map<String, Object> removeCharge(Long chargeId) {
        try {
            log.info("🗑️ REMOVE_CHARGE - Removing charge ID: {}", chargeId);

            // Find the charge
            AdditionalCharge charge = additionalChargeRepository.findById(chargeId)
                    .orElseThrow(() -> new ResourceNotFoundException("Charge not found with ID: " + chargeId));

            BigInteger bookingRequestId = charge.getBookingRequestId();
            log.debug("🗑️ REMOVE_CHARGE - Charge belongs to booking: {}", bookingRequestId);

            // Delete the charge
            additionalChargeRepository.deleteById(chargeId);
            log.debug("🗑️ REMOVE_CHARGE - Charge deleted successfully");

            // Recalculate total additional charges
            List<AdditionalCharge> remainingCharges = additionalChargeRepository.findByBookingRequestId(bookingRequestId);
            BigDecimal total = remainingCharges.stream()
                    .map(AdditionalCharge::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            log.debug("🗑️ REMOVE_CHARGE - Recalculated total: {} ({} remaining charges)",
                    total, remainingCharges.size());

            // Update booking
            BookingRequest booking = bookingRequestRepository.findById(bookingRequestId.intValue())
                    .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + bookingRequestId));

            booking.setAdditionalCharges(total.floatValue());
            booking.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            bookingRequestRepository.save(booking);

            log.info("✅ REMOVE_CHARGE - Charge removed successfully. New total: {}", total);

            return Map.of(
                    "status", "success",
                    "message", "Charge removed successfully",
                    "total", total,
                    "remainingCount", remainingCharges.size()
            );

        } catch (ResourceNotFoundException e) {
            log.error("❌ REMOVE_CHARGE - Not found: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("💥 REMOVE_CHARGE - Error: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to remove charge: " + e.getMessage(), e);
        }
    }

    @Override
    public List<AdditionalChargeDto> getChargesByBookingId(Integer bookingId) {
        try {
            log.info("🔍 GET_CHARGES - Fetching charges for booking: {}", bookingId);

            BigInteger bookingRequestId = BigInteger.valueOf(bookingId);
            List<AdditionalCharge> charges = additionalChargeRepository.findByBookingRequestId(bookingRequestId);

            log.info("✅ GET_CHARGES - Found {} charges for booking: {}", charges.size(), bookingId);

            return charges.stream()
                    .map(mapper::toDto)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("💥 GET_CHARGES - Error fetching charges: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch charges: " + e.getMessage(), e);
        }
    }

    @Override
    public BigDecimal calculateTotalAdditionalCharges(Integer bookingId) {
        try {
            log.debug("🔢 CALCULATE_TOTAL - Calculating total for booking: {}", bookingId);

            BigInteger bookingRequestId = BigInteger.valueOf(bookingId);
            List<AdditionalCharge> charges = additionalChargeRepository.findByBookingRequestId(bookingRequestId);

            BigDecimal total = charges.stream()
                    .map(AdditionalCharge::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            log.debug("✅ CALCULATE_TOTAL - Total: {} for booking: {}", total, bookingId);

            return total;

        } catch (Exception e) {
            log.error("💥 CALCULATE_TOTAL - Error: {}", e.getMessage(), e);
            return BigDecimal.ZERO;
        }
    }
}
