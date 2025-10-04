package com.eptiq.vegobike.dtos;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class BikeResponseDTO {
    private int id;
    private String name;
    private String registrationNumber;
    private String chassisNumber;
    private String engineNumber;
    private int brandId;
    private int categoryId;
    private int modelId;
    private int registrationYearId;
    private int storeId;
    private int price;
    private boolean isPuc;
    private boolean isInsurance;
    private boolean isDocuments;
    private String pucImageUrl;
    private String insuranceImageUrl;
    private String documentImageUrl;
    private List<String> bikeImages;
}
