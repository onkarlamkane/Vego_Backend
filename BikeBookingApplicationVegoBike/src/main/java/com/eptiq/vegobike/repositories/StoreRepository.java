package com.eptiq.vegobike.repositories;

import com.eptiq.vegobike.model.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface StoreRepository extends JpaRepository<Store, Integer> {

    // Search stores by name (for DataTable search functionality)
    Page<Store> findByStoreNameContainingIgnoreCase(String storeName, Pageable pageable);

    // Find stores by location (within radius - for maps feature)
    @Query(value = "SELECT *, " +
            "(6371 * acos(cos(radians(:lat)) * cos(radians(store_latitude)) * " +
            "cos(radians(store_longitude) - radians(:lng)) + " +
            "sin(radians(:lat)) * sin(radians(store_latitude)))) AS distance " +
            "FROM stores WHERE is_active = 1 " +
            "HAVING distance < :radius ORDER BY distance", nativeQuery = true)
    Page<Store> findStoresWithinRadius(@Param("lat") Double latitude,
                                       @Param("lng") Double longitude,
                                       @Param("radius") Double radiusKm,
                                       Pageable pageable);

    // For toggle status - bypass @Where clause
    @Query(value = "SELECT * FROM stores WHERE id = :id", nativeQuery = true)
    Store findByIdIgnoringStatus(@Param("id") Integer id);

    // Count active stores
    long countByIsActive(Integer isActive);

    List<Store> findByIsActive(Integer isActive);
}
