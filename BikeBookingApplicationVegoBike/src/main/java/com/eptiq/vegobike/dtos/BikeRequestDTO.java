package com.eptiq.vegobike.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BikeRequestDTO {
    private String name; // ✅ Added to match DB

    private int brandId;
    private int categoryId;
    private int modelId;
    private int registrationYearId;
    private int storeId;

    private String registrationNumber;
    private String chassisNumber;
    private String engineNumber;
    private String latitude;
    private String longitude;
    private int price;

    private boolean isPuc;
    private boolean isInsurance;
    private boolean isDocuments;

    private MultipartFile pucImage;
    private MultipartFile insuranceImage;
    private MultipartFile documentImage;
    private List<MultipartFile> images; // vehicle images
}
