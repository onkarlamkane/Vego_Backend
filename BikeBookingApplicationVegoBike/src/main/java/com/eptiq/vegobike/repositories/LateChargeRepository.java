package com.eptiq.vegobike.repositories;

import com.eptiq.vegobike.enums.ChargeType;
import com.eptiq.vegobike.model.LateCharge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LateChargeRepository extends JpaRepository<LateCharge, Integer> {

    List<LateCharge> findAllByIsActive(Integer isActive);

    boolean existsByCategoryIdAndChargeType(Integer categoryId, ChargeType chargeType);


}