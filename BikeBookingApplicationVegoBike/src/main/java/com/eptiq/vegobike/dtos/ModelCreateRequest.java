package com.eptiq.vegobike.dtos;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.aspectj.lang.annotation.Around;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModelCreateRequest {
    @NotNull
    private Integer brandId;

    @NotNull
    @Size(min = 1, max = 255)
    private String modelName;

    private String modelImage; // base64 or URL if used

    // getters/setters
}
