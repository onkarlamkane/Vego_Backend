package com.eptiq.vegobike.services;

import com.eptiq.vegobike.dtos.UserDocumentDTO;
import com.eptiq.vegobike.enums.VerificationStatus;
import org.springframework.web.multipart.MultipartFile;

public interface UserDocumentService {

    UserDocumentDTO getUserDocuments(int userId);

    UserDocumentDTO uploadDocuments(UserDocumentDTO dto);

    /**
     * Upload documents with file handling
     * @param userId User ID
     * @param adhaarFrontFile Aadhaar front image file
     * @param adhaarBackFile Aadhaar back image file
     * @param licenseFile Driving license image file
     * @return UserDocumentDTO with uploaded document information
     */
    UserDocumentDTO uploadDocuments(int userId,
                                    MultipartFile adhaarFrontFile,
                                    MultipartFile adhaarBackFile,
                                    MultipartFile licenseFile);

    UserDocumentDTO updateVerificationStatus(int userId, VerificationStatus adhaarFrontStatus,
                                             VerificationStatus adhaarBackStatus,
                                             VerificationStatus licenseStatus);

    VerificationStatus checkDocumentsForBooking(int userId);

    boolean hasAllDocumentsUploaded(int userId);
}
