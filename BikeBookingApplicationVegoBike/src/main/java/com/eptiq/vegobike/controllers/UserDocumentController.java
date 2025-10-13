package com.eptiq.vegobike.controllers;

import com.eptiq.vegobike.dtos.UserDocumentDTO;
import com.eptiq.vegobike.services.UserDocumentService;
import com.eptiq.vegobike.enums.VerificationStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class UserDocumentController {

    private final UserDocumentService service;

    @GetMapping("/{userId}")
    public ResponseEntity<UserDocumentDTO> getDocuments(@PathVariable int userId) {
        try {
            UserDocumentDTO documents = service.getUserDocuments(userId);
            return ResponseEntity.ok(documents);
        } catch (Exception e) {
            log.error("Error fetching documents for user {}: {}", userId, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<UserDocumentDTO> uploadDocuments(@RequestBody UserDocumentDTO dto) {
        return ResponseEntity.ok(service.uploadDocuments(dto));
    }

    @PostMapping(value = "/upload-files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadDocumentFiles(
            @RequestParam("userId") int userId,
            @RequestParam(value = "adhaarFront", required = false) MultipartFile adhaarFrontFile,
            @RequestParam(value = "adhaarBack", required = false) MultipartFile adhaarBackFile,
            @RequestParam(value = "drivingLicense", required = false) MultipartFile licenseFile) {
        try {
            log.info("📄 Document upload request for user: {}", userId);

            UserDocumentDTO result = service.uploadDocuments(userId, adhaarFrontFile, adhaarBackFile, licenseFile);

            log.info("✅ Documents uploaded successfully for user: {}", userId);
            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            log.warn("Invalid request for user {}: {}", userId, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "INVALID_REQUEST",
                    "message", e.getMessage(),
                    "userId", userId,
                    "timestamp", System.currentTimeMillis()
            ));

        } catch (Exception e) {
            log.error("💥 Error uploading documents for user {}: {}", userId, e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "UPLOAD_FAILED",
                    "message", "Failed to upload documents. Please try again.",
                    "userId", userId,
                    "timestamp", System.currentTimeMillis()
            ));
        }
    }

    @PatchMapping("/verify/{userId}")
    public ResponseEntity<?> updateVerificationStatus(
            @PathVariable int userId,
            @RequestParam(required = false) VerificationStatus adhaarFrontStatus,
            @RequestParam(required = false) VerificationStatus adhaarBackStatus,
            @RequestParam(required = false) VerificationStatus licenseStatus) {
        try {
            UserDocumentDTO result = service.updateVerificationStatus(userId, adhaarFrontStatus, adhaarBackStatus, licenseStatus);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("Error updating verification status for user {}: {}", userId, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "UPDATE_FAILED",
                    "message", e.getMessage(),
                    "userId", userId,
                    "timestamp", System.currentTimeMillis()
            ));
        }
    }
}
