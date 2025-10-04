package com.eptiq.vegobike.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BikeSellImageDTO {
    private Long id;
    private Long bikeId;
    private String frontImages;
    private String backImages;
    private String leftImages;
    private String rightImages;
}
