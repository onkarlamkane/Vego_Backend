package com.eptiq.vegobike.repositories;

import com.eptiq.vegobike.model.AdditionalCharge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.math.BigInteger;
import java.util.List;

@Repository
public interface AdditionalChargeRepository extends JpaRepository<AdditionalCharge, Long> {
    List<AdditionalCharge> findByBookingRequestId(BigInteger bookingRequestId);
}
