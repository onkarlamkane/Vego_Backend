package com.eptiq.vegobike.dtos;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingBikeRequest {
    private int bookingId;
    private int type; // 1 = start trip, 2 = end trip
    private String images;
}