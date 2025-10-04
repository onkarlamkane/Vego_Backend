package com.eptiq.vegobike.repositories;

import com.eptiq.vegobike.enums.BikeStatus;
import com.eptiq.vegobike.model.BikeSale;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BikeSaleRepository extends JpaRepository<BikeSale, Long> {

    //List<BikeSale> findByStatus(BikeStatus status);
}
