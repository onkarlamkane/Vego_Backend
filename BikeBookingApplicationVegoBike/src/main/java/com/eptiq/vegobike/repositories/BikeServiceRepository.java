package com.eptiq.vegobike.repositories;

import com.eptiq.vegobike.model.BikeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface BikeServiceRepository extends JpaRepository<BikeService, Long> {

    // Correct column names (statusCode, serviceTypeCode)
    List<BikeService> findByStatusCode(Integer statusCode);
    List<BikeService> findByServiceTypeCode(Integer serviceTypeCode);
    List<BikeService> findByBrandId(Integer brandId);
    List<BikeService> findByCategoryId(Integer categoryId);
    List<BikeService> findByModelId(Integer modelId);



    // Pagination support
    Page<BikeService> findByStatusCode(Integer statusCode, Pageable pageable);
    Page<BikeService> findByServiceTypeCode(Integer serviceTypeCode, Pageable pageable);

    // Example extra methods
    long countByStatusCode(Integer statusCode);
    long countByServiceTypeCode(Integer serviceTypeCode);

    boolean existsByServiceName(String serviceName);

    @Query("SELECT bs FROM BikeService bs WHERE LOWER(bs.serviceName) = LOWER(:serviceName)")
    Optional<BikeService> findByServiceNameIgnoreCase(@Param("serviceName") String serviceName);
    List<BikeService> findByBrandIdAndModelId(Integer brandId, Integer modelId);
    List<BikeService> findByBrandIdAndModelIdAndServiceTypeCode(Integer brandId, Integer modelId, Integer serviceTypeCode);


    List<BikeService> findByBrandIdAndModelIdAndYearId(Integer brandId, Integer modelId, Integer yearId);

}

