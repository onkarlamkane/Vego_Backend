package com.eptiq.vegobike.controllers;

import com.eptiq.vegobike.dtos.BikeSaleEnquiryDTO;
import com.eptiq.vegobike.services.BikeSaleEnquiryService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/bike-sale-enquiries")
@RequiredArgsConstructor
public class BikeSaleEnquiryController {

    private static final Logger logger = LoggerFactory.getLogger(BikeSaleEnquiryController.class);
    private final BikeSaleEnquiryService service;

    @GetMapping("/paged")
    public ResponseEntity<Map<String, Object>> getAllEnquiries(Pageable pageable) {
        String correlationId = UUID.randomUUID().toString().substring(0, 8);
        try {
            Page<BikeSaleEnquiryDTO> enquiries = service.getAllEnquiries(pageable);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", enquiries,
                    "count", enquiries.getTotalElements(),
                    "timestamp", LocalDateTime.now(),
                    "correlationId", correlationId
            ));
        } catch (Exception e) {
            logger.error("ENQUIRY_PAGED_FETCH_FAILED - CorrelationId: {}, Error: {}", correlationId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "error", "ENQUIRY_PAGED_FETCH_FAILED",
                    "message", e.getMessage(),
                    "timestamp", LocalDateTime.now(),
                    "correlationId", correlationId
            ));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getEnquiryById(@PathVariable Long id) {
        String correlationId = UUID.randomUUID().toString().substring(0, 8);
        try {
            BikeSaleEnquiryDTO enquiry = service.getEnquiryById(id);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", enquiry,
                    "timestamp", LocalDateTime.now(),
                    "correlationId", correlationId
            ));
        } catch (Exception e) {
            logger.error("ENQUIRY_FETCH_FAILED - CorrelationId: {}, Error: {}", correlationId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "error", "ENQUIRY_FETCH_FAILED",
                    "message", e.getMessage(),
                    "timestamp", LocalDateTime.now(),
                    "correlationId", correlationId
            ));
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> saveEnquiry(@RequestBody BikeSaleEnquiryDTO dto) {
        String correlationId = UUID.randomUUID().toString().substring(0, 8);
        try {
            BikeSaleEnquiryDTO saved = service.saveEnquiry(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "data", saved,
                    "message", "Enquiry saved successfully",
                    "timestamp", LocalDateTime.now(),
                    "correlationId", correlationId
            ));
        } catch (Exception e) {
            logger.error("ENQUIRY_SAVE_FAILED - CorrelationId: {}, Error: {}", correlationId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "error", "ENQUIRY_SAVE_FAILED",
                    "message", e.getMessage(),
                    "timestamp", LocalDateTime.now(),
                    "correlationId", correlationId
            ));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateEnquiry(@PathVariable Long id, @RequestBody BikeSaleEnquiryDTO dto) {
        String correlationId = UUID.randomUUID().toString().substring(0, 8);
        try {
            BikeSaleEnquiryDTO updated = service.updateEnquiry(id, dto);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", updated,
                    "message", "Enquiry updated successfully",
                    "timestamp", LocalDateTime.now(),
                    "correlationId", correlationId
            ));
        } catch (Exception e) {
            logger.error("ENQUIRY_UPDATE_FAILED - CorrelationId: {}, Error: {}", correlationId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "error", "ENQUIRY_UPDATE_FAILED",
                    "message", e.getMessage(),
                    "timestamp", LocalDateTime.now(),
                    "correlationId", correlationId
            ));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteEnquiry(@PathVariable Long id) {
        String correlationId = UUID.randomUUID().toString().substring(0, 8);
        try {
            service.deleteEnquiry(id);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Enquiry deleted successfully",
                    "timestamp", LocalDateTime.now(),
                    "correlationId", correlationId
            ));
        } catch (Exception e) {
            logger.error("ENQUIRY_DELETE_FAILED - CorrelationId: {}, Error: {}", correlationId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "error", "ENQUIRY_DELETE_FAILED",
                    "message", e.getMessage(),
                    "timestamp", LocalDateTime.now(),
                    "correlationId", correlationId
            ));
        }
    }

    @GetMapping("/by-id/{id}")
    public ResponseEntity<Map<String, Object>> getEnquiry(@PathVariable Long id) {
        return getEnquiryById(id); // ✅ reuse same logic
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateStatus(@PathVariable Long id, @RequestParam String status) {
        String correlationId = UUID.randomUUID().toString().substring(0, 8);
        try {
            BikeSaleEnquiryDTO updated = service.updateEnquiryStatus(id, status);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", updated,
                    "message", "Status updated successfully",
                    "timestamp", LocalDateTime.now(),
                    "correlationId", correlationId
            ));
        } catch (Exception e) {
            logger.error("ENQUIRY_STATUS_UPDATE_FAILED - CorrelationId: {}, Error: {}", correlationId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "error", "ENQUIRY_STATUS_UPDATE_FAILED",
                    "message", e.getMessage(),
                    "timestamp", LocalDateTime.now(),
                    "correlationId", correlationId
            ));
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllEnquiries() {
        String correlationId = UUID.randomUUID().toString().substring(0, 8);
        try {
            List<BikeSaleEnquiryDTO> enquiries = service.getAllEnquiries();
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", enquiries,
                    "count", enquiries.size(),
                    "timestamp", LocalDateTime.now(),
                    "correlationId", correlationId
            ));
        } catch (Exception e) {
            logger.error("ENQUIRY_FETCH_ALL_FAILED - CorrelationId: {}, Error: {}", correlationId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "error", "ENQUIRY_FETCH_ALL_FAILED",
                    "message", e.getMessage(),
                    "timestamp", LocalDateTime.now(),
                    "correlationId", correlationId
            ));
        }
    }
}

