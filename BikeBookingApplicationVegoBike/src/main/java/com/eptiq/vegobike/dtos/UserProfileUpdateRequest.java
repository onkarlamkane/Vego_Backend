package com.eptiq.vegobike.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileUpdateRequest {
    private String name;
    private String address;
    private String accountNumber;
    private String ifsc;
    private String upiId;
    private String profileImageName;
    private MultipartFile profileImage;
}