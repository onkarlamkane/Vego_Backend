package com.eptiq.vegobike.dtos;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingStatusDto {
    private int id;
    private String name;
    private String colorCode;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
