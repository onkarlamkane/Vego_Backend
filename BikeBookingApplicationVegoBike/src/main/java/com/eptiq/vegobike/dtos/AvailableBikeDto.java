package com.eptiq.vegobike.dtos;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvailableBikeDto {
    private int id;
    private String name;
    private int categoryId;
    private Integer modelId;
    private String registrationNumber;
    private String storeName;
    private String mainImageUrl;
    private List<PriceListDTO> packages;

    // In AvailableBikeDto
    public AvailableBikeDto(int id, String name, int categoryId, Integer modelId,
                            String registrationNumber, String storeName, String mainImageUrl) {
        this.id = id;
        this.name = name;
        this.categoryId = categoryId;
        this.modelId = modelId;
        this.registrationNumber = registrationNumber;
        this.storeName = storeName;
        this.mainImageUrl = mainImageUrl;
    }


}