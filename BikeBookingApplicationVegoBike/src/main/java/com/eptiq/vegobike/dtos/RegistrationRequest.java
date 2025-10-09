package com.eptiq.vegobike.dtos;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
@Getter
@Setter
@Data
public class
RegistrationRequest {
        private String name;
        private String phoneNumber;
        private String password;
        private String alternateNumber;
        private String email;
        private String address;
        private String accountNumber;
        private String ifsc;
        private String upiId;
        @JsonIgnore
        private MultipartFile profileImage;// ✅ upload file
        private String profileImageName;
        private String profileImagePath;
        private Integer roleId;      // 2 for Store Manager, 3 for User
        private Long storeId;     // Only for store managers

}
