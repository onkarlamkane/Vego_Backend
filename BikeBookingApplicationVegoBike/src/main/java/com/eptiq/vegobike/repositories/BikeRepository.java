//package com.eptiq.vegobike.repositories;
//import com.eptiq.vegobike.model.Bike;
//import io.lettuce.core.dynamic.annotation.Param;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.stereotype.Repository;
//
//import java.util.Date;
//import java.util.List;
//
//@Repository
//public interface BikeRepository extends JpaRepository<Bike, Integer> {
//
//    @Query(value = "SELECT b.*, s.store_name FROM bikes b "
//            + "LEFT JOIN stores s ON b.store_id = s.id "
//            + "WHERE b.is_active = 1 "
//            + "AND (:search IS NULL OR LOWER(b.name) LIKE LOWER(CONCAT('%', :search, '%')) "
//            + "OR LOWER(b.registration_number) LIKE LOWER(CONCAT('%', :search, '%'))) "
//            + "AND NOT EXISTS ("
//            + "   SELECT 1 FROM booking_requests br "
//            + "   WHERE br.vehicle_id = b.id "
//            + "     AND br.booking_status IN (:activeStatuses) "
//            + "     AND br.start_date <= :selectedEndDate "
//            + "     AND br.end_date >= :selectedStartDate"
//            + ")", nativeQuery = true)
//    Page<Bike> findAvailableBikes(@Param("search") String search,
//                                  @Param("activeStatuses") List<Integer> activeStatuses,
//                                  @Param("selectedStartDate") Date selectedStartDate,
//                                  @Param("selectedEndDate") Date selectedEndDate,
//                                  Pageable pageable);
//}

// src/main/java/com/eptiq/vegobike/repositories/BikeRepository.java

package com.eptiq.vegobike.repositories;

import com.eptiq.vegobike.dtos.AvailableBikeDto;
import com.eptiq.vegobike.dtos.AvailableBikeRow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface BikeRepository extends JpaRepository<com.eptiq.vegobike.model.Bike, Integer> {

    // BikeRepository (unchanged alias, optional fallback via COALESCE)
    @Query(
            value = """
    SELECT 
      b.id                  AS id,
      b.name                AS name,
      b.category_id         AS categoryId,
      b.model_id            AS modelId,
      b.registration_number AS registrationNumber,
      s.store_name          AS storeName,
      COALESCE((
        SELECT bi.images
        FROM bike_images bi
        WHERE bi.bike_id = b.id
        ORDER BY bi.created_at DESC, bi.id DESC
        LIMIT 1
      ), b.document_image)  AS mainImageUrl
    FROM bikes b
    LEFT JOIN stores s ON s.id = b.store_id
    WHERE b.is_active = 1
      AND (
           :search IS NULL 
           OR LOWER(b.name) LIKE LOWER(CONCAT('%', :search, '%'))
           OR LOWER(b.registration_number) LIKE LOWER(CONCAT('%', :search, '%'))
      )
      AND NOT EXISTS (
        SELECT 1 
        FROM booking_requests br
        WHERE br.vehicle_id = b.id
          AND br.booking_status IN (:activeStatuses)
          AND br.start_date <= :selectedEndDate
          AND br.end_date   >= :selectedStartDate
      )
    """,
            countQuery = """
    SELECT COUNT(*)
    FROM bikes b
    WHERE b.is_active = 1
      AND (
           :search IS NULL 
           OR LOWER(b.name) LIKE LOWER(CONCAT('%', :search, '%'))
           OR LOWER(b.registration_number) LIKE LOWER(CONCAT('%', :search, '%'))
      )
      AND NOT EXISTS (
        SELECT 1 
        FROM booking_requests br
        WHERE br.vehicle_id = b.id
          AND br.booking_status IN (:activeStatuses)
          AND br.start_date <= :selectedEndDate
          AND br.end_date   >= :selectedStartDate
      )
    """,
            nativeQuery = true
    )
    Page<AvailableBikeRow> findAvailableBikeRows(
            @Param("search") String search,
            @Param("activeStatuses") List<Integer> activeStatuses,
            @Param("selectedStartDate") Date selectedStartDate,
            @Param("selectedEndDate") Date selectedEndDate,
            Pageable pageable
    );

}
