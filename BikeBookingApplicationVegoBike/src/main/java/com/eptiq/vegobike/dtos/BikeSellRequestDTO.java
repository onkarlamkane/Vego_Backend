package com.eptiq.vegobike.dtos;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BikeSellRequestDTO {
    private BikeSellerDetailDTO sellerDetail;
    private BikeSaleDTO bikeSale;
    private BikeSellImageDTO bikeImages;
}
