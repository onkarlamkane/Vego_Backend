package com.eptiq.vegobike.controllers;

import com.eptiq.vegobike.dtos.AdditionalChargeDto;
import com.eptiq.vegobike.dtos.SaveAdditionalChargesRequest;
import com.eptiq.vegobike.services.AdditionalChargeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/additional-charges")
@RequiredArgsConstructor
@Slf4j
public class AdditionalChargeController {

    private final AdditionalChargeService additionalChargeService;

    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> saveAdditionalCharges(
            @RequestBody SaveAdditionalChargesRequest request) {
        log.info("📥 POST /api/additional-charges/save - Booking ID: {}", request.getBookingId());

        try {
            Map<String, Object> response = additionalChargeService.saveAdditionalCharges(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ Error saving charges: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @PostMapping("/remove")
    public ResponseEntity<Map<String, Object>> removeCharge(@RequestParam Long id) {
        log.info("📥 POST /api/additional-charges/remove - Charge ID: {}", id);

        try {
            Map<String, Object> response = additionalChargeService.removeCharge(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ Error removing charge: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<List<AdditionalChargeDto>> getChargesByBookingId(
            @PathVariable Integer bookingId) {
        log.info("📥 GET /api/additional-charges/booking/{} - Fetching charges", bookingId);

        try {
            List<AdditionalChargeDto> charges = additionalChargeService.getChargesByBookingId(bookingId);
            return ResponseEntity.ok(charges);
        } catch (Exception e) {
            log.error("❌ Error fetching charges: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
