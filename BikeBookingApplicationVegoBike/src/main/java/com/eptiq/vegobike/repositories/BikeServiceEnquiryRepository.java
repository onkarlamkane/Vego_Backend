package com.eptiq.vegobike.repositories;

import com.eptiq.vegobike.model.BikeServiceEnquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BikeServiceEnquiryRepository extends JpaRepository<BikeServiceEnquiry, Long> {
}
