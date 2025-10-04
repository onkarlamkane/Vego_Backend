package com.eptiq.vegobike.dtos;

import lombok.Data;
import java.sql.Timestamp;

@Data
public class VehicleTypeDto {
    private Integer id;
    private String name;
    private Integer isActive;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
