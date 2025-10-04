package com.eptiq.vegobike.repositories;

import com.eptiq.vegobike.model.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BrandRepository extends JpaRepository<Brand, Integer> {

    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END " +
            "FROM Brand b " +
            "WHERE LOWER(CAST(b.brandName AS string)) = LOWER(:brandName)")
    boolean existsByBrandNameIgnoreCase(@Param("brandName") String brandName);

    Page<Brand> findByIsActive(Integer isActive, Pageable pageable);

    @Query(value = "select * from brands where id = :id", nativeQuery = true)
    Brand findAnyById(@Param("id") Integer id);

}



