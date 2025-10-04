package com.eptiq.vegobike.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.eptiq.vegobike.enums.VerificationStatus;
import lombok.Data;
import java.sql.Timestamp;
@Data
public class UserDocumentDTO {
    private Integer id;
    private Integer userId;

    // Verification status enums
    private VerificationStatus adhaarFrontStatus;
    private String adhaarFrontImage;        // File path stored in DB
    private String adhaarFrontImageUrl;     // Public URL for frontend

    private VerificationStatus adhaarBackStatus;
    private String adhaarBackImage;         // File path stored in DB
    private String adhaarBackImageUrl;      // Public URL for frontend

    private VerificationStatus licenseStatus;
    private String drivingLicenseImage;     // File path stored in DB
    private String drivingLicenseImageUrl;  // Public URL for frontend

    private Boolean active;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp updatedAt;
}
