package com.eptiq.vegobike.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BikeSellImageDTO {
    private Long id;
    private Long bikeId;
    private String frontImages;
    private String backImages;
    private String leftImages;
    private String rightImages;

    private MultipartFile frontImageFile;
    private MultipartFile backImageFile;
    private MultipartFile leftImageFile;
    private MultipartFile rightImageFile;
}
