package com.eptiq.vegobike.repositories;

import com.eptiq.vegobike.model.BikeImage;
import com.eptiq.vegobike.model.BikeSellImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BikeSellImageRepository extends JpaRepository<BikeSellImage, Long> {
    BikeSellImage findByBikeSaleId(Long bikeId);
}

