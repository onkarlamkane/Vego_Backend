package com.eptiq.vegobike.services.impl;

import com.eptiq.vegobike.dtos.UserDocumentDTO;
import com.eptiq.vegobike.exceptions.DocumentVerificationException;
import com.eptiq.vegobike.mappers.UserDocumentMapper;
import com.eptiq.vegobike.model.UserDocument;
import com.eptiq.vegobike.exceptions.NotFoundException;
import com.eptiq.vegobike.repositories.UserDocumentRepository;
import com.eptiq.vegobike.services.UserDocumentService;
import com.eptiq.vegobike.enums.VerificationStatus;
import com.eptiq.vegobike.utils.ImageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDocumentServiceImpl implements UserDocumentService {

    private final UserDocumentRepository repository;
    private final UserDocumentMapper mapper;
    private final ImageUtils imageUtils; // Add ImageUtils dependency

    @Override
    public UserDocumentDTO getUserDocuments(int userId) {
        UserDocument doc = repository.findByUserIdAndIsActive(userId, 1)
                .orElseThrow(() -> new NotFoundException("Documents not found for user ID: " + userId));

        UserDocumentDTO dto = mapper.toDTO(doc);

        // Add public URLs for frontend
        if (doc.getAdhaarFrontImage() != null) {
            dto.setAdhaarFrontImageUrl(imageUtils.getPublicUrl(doc.getAdhaarFrontImage()));
        }
        if (doc.getAdhaarBackImage() != null) {
            dto.setAdhaarBackImageUrl(imageUtils.getPublicUrl(doc.getAdhaarBackImage()));
        }
        if (doc.getDrivingLicenseImage() != null) {
            dto.setDrivingLicenseImageUrl(imageUtils.getPublicUrl(doc.getDrivingLicenseImage()));
        }

        return dto;
    }

    @Override
    @Transactional
    public UserDocumentDTO uploadDocuments(UserDocumentDTO dto) {
        UserDocument entity;

        if (dto.getUserId() != null) {
            // Check if documents already exist for this user
            entity = repository.findByUserIdAndIsActive(dto.getUserId(), 1)
                    .map(existing -> {
                        mapper.updateEntityFromDTO(dto, existing);
                        return existing;
                    })
                    .orElseGet(() -> mapper.toEntity(dto));
        } else {
            entity = mapper.toEntity(dto);
        }

        UserDocument saved = repository.save(entity);
        return mapper.toDTO(saved);
    }

    @Override
    @Transactional
    public UserDocumentDTO uploadDocuments(int userId,
                                           MultipartFile adhaarFrontFile,
                                           MultipartFile adhaarBackFile,
                                           MultipartFile licenseFile) {
        try {
            log.info("📄 Starting document upload for user ID: {}", userId);

            // Validate that at least one file is provided
            if ((adhaarFrontFile == null || adhaarFrontFile.isEmpty()) &&
                    (adhaarBackFile == null || adhaarBackFile.isEmpty()) &&
                    (licenseFile == null || licenseFile.isEmpty())) {
                throw new IllegalArgumentException("At least one document file must be provided");
            }

            // Get existing document record or create new one
            UserDocument entity = repository.findByUserIdAndIsActive(userId, 1)
                    .orElse(new UserDocument());

            if (entity.getId() == null) {
                // New record
                entity.setUserId(userId);
                entity.setIsActive(1);
                entity.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                log.debug("📄 Creating new document record for user: {}", userId);
            } else {
                log.debug("📄 Updating existing document record for user: {}", userId);
            }

            // Handle Aadhaar Front upload
            if (adhaarFrontFile != null && !adhaarFrontFile.isEmpty()) {
                // Delete old file if exists
                if (entity.getAdhaarFrontImage() != null) {
                    boolean deleted = imageUtils.deleteImage(entity.getAdhaarFrontImage());
                    log.debug("📄 Old Aadhaar front image deletion: {}", deleted ? "Success" : "Failed/NotFound");
                }

                // Store new file
                String adhaarFrontPath = imageUtils.storeAadhaarFrontImage(adhaarFrontFile, userId);
                entity.setAdhaarFrontImage(adhaarFrontPath);
                entity.setIsAdhaarFrontVerified(0); // Reset to pending when new file uploaded
                log.debug("📄 Stored new Aadhaar front image: {}", adhaarFrontPath);
            }

            // Handle Aadhaar Back upload
            if (adhaarBackFile != null && !adhaarBackFile.isEmpty()) {
                // Delete old file if exists
                if (entity.getAdhaarBackImage() != null) {
                    boolean deleted = imageUtils.deleteImage(entity.getAdhaarBackImage());
                    log.debug("📄 Old Aadhaar back image deletion: {}", deleted ? "Success" : "Failed/NotFound");
                }

                // Store new file
                String adhaarBackPath = imageUtils.storeAadhaarBackImage(adhaarBackFile, userId);
                entity.setAdhaarBackImage(adhaarBackPath);
                entity.setIsAdhaarBackVerified(0); // Reset to pending when new file uploaded
                log.debug("📄 Stored new Aadhaar back image: {}", adhaarBackPath);
            }

            // Handle License upload
            if (licenseFile != null && !licenseFile.isEmpty()) {
                // Delete old file if exists
                if (entity.getDrivingLicenseImage() != null) {
                    boolean deleted = imageUtils.deleteImage(entity.getDrivingLicenseImage());
                    log.debug("📄 Old driving license image deletion: {}", deleted ? "Success" : "Failed/NotFound");
                }

                // Store new file
                String licensePath = imageUtils.storeDrivingLicenseImage(licenseFile, userId);
                entity.setDrivingLicenseImage(licensePath);
                entity.setIsLicenseVerified(0); // Reset to pending when new file uploaded
                log.debug("📄 Stored new driving license image: {}", licensePath);
            }

            // Update timestamp
            entity.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

            // Save to database
            UserDocument saved = repository.save(entity);

            // Convert to DTO and add public URLs
            UserDocumentDTO result = mapper.toDTO(saved);

            if (saved.getAdhaarFrontImage() != null) {
                result.setAdhaarFrontImageUrl(imageUtils.getPublicUrl(saved.getAdhaarFrontImage()));
            }
            if (saved.getAdhaarBackImage() != null) {
                result.setAdhaarBackImageUrl(imageUtils.getPublicUrl(saved.getAdhaarBackImage()));
            }
            if (saved.getDrivingLicenseImage() != null) {
                result.setDrivingLicenseImageUrl(imageUtils.getPublicUrl(saved.getDrivingLicenseImage()));
            }

            log.info("✅ Documents uploaded successfully for user ID: {}", userId);
            return result;

        } catch (Exception e) {
            log.error("💥 Error uploading documents for user {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Failed to upload documents: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public UserDocumentDTO updateVerificationStatus(int userId, VerificationStatus adhaarFrontStatus,
                                                    VerificationStatus adhaarBackStatus,
                                                    VerificationStatus licenseStatus) {
        UserDocument doc = repository.findByUserIdAndIsActive(userId, 1)
                .orElseThrow(() -> new NotFoundException("Documents not found for user ID: " + userId));

        if (adhaarFrontStatus != null) {
            doc.setIsAdhaarFrontVerified(adhaarFrontStatus.getCode());
            log.debug("📄 Updated Aadhaar front status to: {} for user: {}", adhaarFrontStatus, userId);
        }
        if (adhaarBackStatus != null) {
            doc.setIsAdhaarBackVerified(adhaarBackStatus.getCode());
            log.debug("📄 Updated Aadhaar back status to: {} for user: {}", adhaarBackStatus, userId);
        }
        if (licenseStatus != null) {
            doc.setIsLicenseVerified(licenseStatus.getCode());
            log.debug("📄 Updated license status to: {} for user: {}", licenseStatus, userId);
        }

        doc.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        UserDocument saved = repository.save(doc);

        log.info("✅ Verification status updated for user: {}", userId);
        return mapper.toDTO(saved);
    }

    @Override
    public VerificationStatus checkDocumentsForBooking(int userId) {
        try {
            UserDocumentDTO userDoc = getUserDocuments(userId);

            boolean adhaarFrontUploaded = userDoc.getAdhaarFrontImage() != null && !userDoc.getAdhaarFrontImage().trim().isEmpty();
            boolean adhaarBackUploaded = userDoc.getAdhaarBackImage() != null && !userDoc.getAdhaarBackImage().trim().isEmpty();
            boolean licenseUploaded = userDoc.getDrivingLicenseImage() != null && !userDoc.getDrivingLicenseImage().trim().isEmpty();

            if (!adhaarFrontUploaded || !adhaarBackUploaded || !licenseUploaded) {
                log.warn("📄 Documents not fully uploaded for user {}, treating as PENDING", userId);
                // Instead of throwing, return PENDING to indicate docs not fully present
                return VerificationStatus.PENDING;
            }

            VerificationStatus adhaarFrontStatus = userDoc.getAdhaarFrontStatus();
            VerificationStatus adhaarBackStatus = userDoc.getAdhaarBackStatus();
            VerificationStatus licenseStatus = userDoc.getLicenseStatus();

            if (adhaarFrontStatus == VerificationStatus.REJECTED ||
                    adhaarBackStatus == VerificationStatus.REJECTED ||
                    licenseStatus == VerificationStatus.REJECTED) {
                log.warn("📄 Document(s) rejected for user {}", userId);
                return VerificationStatus.REJECTED;
            }

            if (adhaarFrontStatus == VerificationStatus.VERIFIED &&
                    adhaarBackStatus == VerificationStatus.VERIFIED &&
                    licenseStatus == VerificationStatus.VERIFIED) {
                log.info("✅ All documents verified for user {}", userId);
                return VerificationStatus.VERIFIED;
            }

            // Some documents possibly pending further verification
            log.info("⏳ Some documents pending verification for user {}", userId);
            return VerificationStatus.PENDING;

        } catch (NotFoundException e) {
            log.warn("📄 No documents found for user {}, treating as PENDING", userId);
            // Return PENDING status instead of throwing exception
            return VerificationStatus.PENDING;
        } catch (Exception e) {
            log.error("📄 Error checking document status for user {}: {}", userId, e.getMessage());
            throw new DocumentVerificationException("Unable to verify document status. Please try again later.");
        }
    }

    @Override
    public boolean hasAllDocumentsUploaded(int userId) {
        try {
            UserDocumentDTO userDoc = getUserDocuments(userId);

            boolean adhaarFrontUploaded = userDoc.getAdhaarFrontImage() != null && !userDoc.getAdhaarFrontImage().trim().isEmpty();
            boolean adhaarBackUploaded = userDoc.getAdhaarBackImage() != null && !userDoc.getAdhaarBackImage().trim().isEmpty();
            boolean licenseUploaded = userDoc.getDrivingLicenseImage() != null && !userDoc.getDrivingLicenseImage().trim().isEmpty();

            return adhaarFrontUploaded && adhaarBackUploaded && licenseUploaded;

        } catch (NotFoundException e) {
            return false;
        } catch (Exception e) {
            log.error("Error checking document upload status for user {}: {}", userId, e.getMessage());
            return false;
        }
    }
}
