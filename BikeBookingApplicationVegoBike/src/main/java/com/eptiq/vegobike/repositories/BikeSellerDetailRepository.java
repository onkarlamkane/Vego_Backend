package com.eptiq.vegobike.repositories;

import com.eptiq.vegobike.model.BikeSellerDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BikeSellerDetailRepository extends JpaRepository<BikeSellerDetail, Long> { // changed Long -> Integer

    Optional<BikeSellerDetail> findByBikeId(int bikeId); // returning Optional is better for null safety
}
