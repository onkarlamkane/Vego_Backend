package com.eptiq.vegobike.repositories;


import com.eptiq.vegobike.model.Offer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OfferRepository extends JpaRepository<Offer, Integer> {

    List<Offer> findByIsActive(Integer isActive);

    Optional<Offer> findByOfferCode(String offerCode);



}
