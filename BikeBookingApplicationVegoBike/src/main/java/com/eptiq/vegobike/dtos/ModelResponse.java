package com.eptiq.vegobike.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModelResponse {
    private Integer id;
    private String modelName;
    private Integer brandId;
    private String brandName;
    private Integer isActive;
    private String modelImage;


}