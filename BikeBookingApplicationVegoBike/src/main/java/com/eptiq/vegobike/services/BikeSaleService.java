package com.eptiq.vegobike.services;
import com.eptiq.vegobike.dtos.BikeSellImageDTO;
import com.eptiq.vegobike.dtos.BikeSaleDTO;
public interface BikeSaleService {
    BikeSaleDTO getBikeSaleById(Long id);
    BikeSaleDTO updateBikeSale(Long id, BikeSaleDTO bikeSaleDTO);
    BikeSellImageDTO updateBikeImages(Long bikeId, BikeSellImageDTO bikeSellImageDTO);
    BikeSaleDTO saveBikeSale(BikeSaleDTO bikeSaleDTO);

}
