package com.eptiq.vegobike.repositories;

import com.eptiq.vegobike.mappers.BikeSaleMapper;
import com.eptiq.vegobike.model.BikeSale;
import com.eptiq.vegobike.model.BikeSaleEnquiry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BikeSaleRepository extends JpaRepository<BikeSale, Long> {
    List<BikeSale> findByStatus(String status);
    List<BikeSale> findBySellId(String sellId);
    List<BikeSale> findAllByOrderByIdDesc();
}
