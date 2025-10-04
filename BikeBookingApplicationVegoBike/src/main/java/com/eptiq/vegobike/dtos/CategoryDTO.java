package com.eptiq.vegobike.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {

    private Integer id;

    @NotBlank(message = "Category name is required")
    @Size(min = 2, max = 255, message = "Category name must be between 2 and 255 characters")
    private String categoryName;

    private String image;

    private Integer isActive;

    private Timestamp createdAt;

    private Timestamp updatedAt;

    private Integer vehicleTypeId;

    // Constructor for create requests
    public CategoryDTO(String categoryName) {
        this.categoryName = categoryName;
        this.isActive = 1;
    }
}
