package com.eptiq.vegobike.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrandDTO {

    private Integer id;

    @NotBlank(message = "Brand name is required")
    @Size(min = 2, max = 255, message = "Brand name must be between 2 and 255 characters")
    private String brandName;

    private String brandImage;

    private Integer categoryId;  // optional if not used

    private Integer isActive;


    private Timestamp createdAt;

    private Timestamp updatedAt;

}