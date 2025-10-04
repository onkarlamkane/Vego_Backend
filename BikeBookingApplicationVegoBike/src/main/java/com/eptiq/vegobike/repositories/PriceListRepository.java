//package com.eptiq.vegobike.repositories;
//
//import com.eptiq.vegobike.model.PriceList;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.Optional;
//
//@Repository
//public interface PriceListRepository extends JpaRepository<PriceList, Long> {
//
//    // ✅ Core query methods
//    List<PriceList> findByIsActive(Integer isActive);
//    Page<PriceList> findByIsActive(Integer isActive, Pageable pageable);
//
//    List<PriceList> findByCategoryId(Integer categoryId);
//    List<PriceList> findByCategoryIdAndIsActive(Integer categoryId, Integer isActive);
//
//    List<PriceList> findByDays(Integer days);
//    List<PriceList> findByDaysAndIsActive(Integer days, Integer isActive);
//
//    // ✅ Price range queries
//    List<PriceList> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
//
//    @Query("SELECT pl FROM PriceList pl WHERE pl.categoryId = :categoryId AND pl.days = :days AND pl.isActive = 1")
//    Optional<PriceList> findActivePriceByCategoryAndDays(@Param("categoryId") Integer categoryId,
//                                                         @Param("days") Integer days);
//
//    // ✅ Hourly rates
//    @Query("SELECT pl FROM PriceList pl WHERE pl.days = 0 AND pl.isActive = 1")
//    List<PriceList> findActiveHourlyRates();
//
//    // ✅ Daily rates
//    @Query("SELECT pl FROM PriceList pl WHERE pl.days > 0 AND pl.isActive = 1")
//    List<PriceList> findActiveDailyRates();
//
//    // ✅ Category-based queries
//    @Query("SELECT pl FROM PriceList pl WHERE pl.categoryId IN :categoryIds AND pl.isActive = 1")
//    List<PriceList> findActivePricesByCategories(@Param("categoryIds") List<Integer> categoryIds);
//
//    // ✅ Price comparison
//    @Query("SELECT pl FROM PriceList pl WHERE pl.price <= :maxPrice AND pl.isActive = 1 ORDER BY pl.price ASC")
//    List<PriceList> findAffordablePrices(@Param("maxPrice") BigDecimal maxPrice);
//
//    // ✅ Duplicate check
//    @Query("SELECT COUNT(pl) FROM PriceList pl WHERE pl.categoryId = :categoryId AND pl.days = :days AND pl.isActive = 1 AND (:id IS NULL OR pl.id != :id)")
//    long countDuplicatePricing(@Param("categoryId") Integer categoryId,
//                               @Param("days") Integer days,
//                               @Param("id") Long id);
//
//    // ✅ Statistics
//    @Query("SELECT AVG(pl.price) FROM PriceList pl WHERE pl.categoryId = :categoryId AND pl.isActive = 1")
//    BigDecimal getAveragePriceByCategory(@Param("categoryId") Integer categoryId);
//
//    @Query("SELECT MIN(pl.price) FROM PriceList pl WHERE pl.categoryId = :categoryId AND pl.isActive = 1")
//    BigDecimal getMinPriceByCategory(@Param("categoryId") Integer categoryId);
//
//    @Query("SELECT MAX(pl.price) FROM PriceList pl WHERE pl.categoryId = :categoryId AND pl.isActive = 1")
//    BigDecimal getMaxPriceByCategory(@Param("categoryId") Integer categoryId);
//}


package com.eptiq.vegobike.repositories;

import com.eptiq.vegobike.model.PriceList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface PriceListRepository extends JpaRepository<PriceList, Long> {

    // Core query methods
    List<PriceList> findByIsActive(Integer isActive);
    Page<PriceList> findByIsActive(Integer isActive, Pageable pageable);

    List<PriceList> findByCategoryId(Integer categoryId);
    List<PriceList> findByCategoryIdAndIsActive(Integer categoryId, Integer isActive);

    // NEW: batch by categories (used by service)
    List<PriceList> findByCategoryIdInAndIsActive(Collection<Integer> categoryIds, Integer isActive);

    // Optional: ordered variant for consistent package listing
    List<PriceList> findByCategoryIdInAndIsActiveOrderByDaysAsc(Collection<Integer> categoryIds, Integer isActive);

    // Optional: exact pick for a single category/days if needed
    Optional<PriceList> findFirstByCategoryIdAndDaysAndIsActive(Integer categoryId, Integer days, Integer isActive);

    List<PriceList> findByDays(Integer days);
    List<PriceList> findByDaysAndIsActive(Integer days, Integer isActive);

    // Price range queries
    List<PriceList> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    @Query("SELECT pl FROM PriceList pl WHERE pl.categoryId = :categoryId AND pl.days = :days AND pl.isActive = 1")
    Optional<PriceList> findActivePriceByCategoryAndDays(@Param("categoryId") Integer categoryId,
                                                         @Param("days") Integer days);

    // Hourly rates
    @Query("SELECT pl FROM PriceList pl WHERE pl.days = 0 AND pl.isActive = 1")
    List<PriceList> findActiveHourlyRates();

    // Daily rates
    @Query("SELECT pl FROM PriceList pl WHERE pl.days > 0 AND pl.isActive = 1")
    List<PriceList> findActiveDailyRates();

    // Category-based queries (existing)
    @Query("SELECT pl FROM PriceList pl WHERE pl.categoryId IN :categoryIds AND pl.isActive = 1")
    List<PriceList> findActivePricesByCategories(@Param("categoryIds") List<Integer> categoryIds);

    // Price comparison
    @Query("SELECT pl FROM PriceList pl WHERE pl.price <= :maxPrice AND pl.isActive = 1 ORDER BY pl.price ASC")
    List<PriceList> findAffordablePrices(@Param("maxPrice") BigDecimal maxPrice);

    // Duplicate check
    @Query("SELECT COUNT(pl) FROM PriceList pl WHERE pl.categoryId = :categoryId AND pl.days = :days AND pl.isActive = 1 AND (:id IS NULL OR pl.id != :id)")
    long countDuplicatePricing(@Param("categoryId") Integer categoryId,
                               @Param("days") Integer days,
                               @Param("id") Long id);

    // Statistics
    @Query("SELECT AVG(pl.price) FROM PriceList pl WHERE pl.categoryId = :categoryId AND pl.isActive = 1")
    BigDecimal getAveragePriceByCategory(@Param("categoryId") Integer categoryId);

    @Query("SELECT MIN(pl.price) FROM PriceList pl WHERE pl.categoryId = :categoryId AND pl.isActive = 1")
    BigDecimal getMinPriceByCategory(@Param("categoryId") Integer categoryId);

    @Query("SELECT MAX(pl.price) FROM PriceList pl WHERE pl.categoryId = :categoryId AND pl.isActive = 1")
    BigDecimal getMaxPriceByCategory(@Param("categoryId") Integer categoryId);

    // Optional: projection for lean package reads
    interface PricePackageProjection {
        Integer getCategoryId();
        Integer getDays();
        BigDecimal getPrice();
        BigDecimal getDeposit();
        BigDecimal getHourlyChargeAmount();
    }

    @Query("""
        SELECT pl.categoryId AS categoryId,
               pl.days        AS days,
               pl.price       AS price,
               pl.deposit     AS deposit,
               pl.hourlyChargeAmount AS hourlyChargeAmount
        FROM PriceList pl
        WHERE pl.categoryId IN :categoryIds AND pl.isActive = 1
        ORDER BY pl.categoryId ASC, pl.days ASC
        """)
    List<PricePackageProjection> findActivePackageProjectionByCategoryIds(@Param("categoryIds") Collection<Integer> categoryIds);
}
