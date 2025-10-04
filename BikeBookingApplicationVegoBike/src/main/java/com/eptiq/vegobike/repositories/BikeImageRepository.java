package com.eptiq.vegobike.repositories;

import com.eptiq.vegobike.model.BikeImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface BikeImageRepository extends JpaRepository<BikeImage, Long> {

    // ✅ Core methods used in your BikeServiceImpl

    /**
     * Find all bike images by bike ID
     * Used in: getBikeImages(), deleteBike()
     */
    List<BikeImage> findByBike_Id(Integer bikeId);



    /**
     * Delete all bike images by bike ID
     * Used in: updateBike(), deleteBike()
     */
    @Modifying
    @Transactional
    void deleteByBike_Id(Integer bikeId);

    // ✅ Additional useful methods for bike image management

    /**
     * Find bike images by bike ID with pagination
     */
    Page<BikeImage> findByBike_Id(Integer bikeId, Pageable pageable);

    /**
     * Count bike images for a specific bike
     */
    long countByBike_Id(Integer bikeId);

    /**
     * Find bike images by bike ID ordered by creation date
     */
    List<BikeImage> findByBike_IdOrderByCreatedAtAsc(Integer bikeId);

    /**
     * Find bike images by bike ID ordered by creation date (descending)
     */
    List<BikeImage> findByBike_IdOrderByCreatedAtDesc(Integer bikeId);

    /**
     * Check if bike has any images
     */
    boolean existsByBike_Id(Integer bikeId);

    /**
     * Find bike images by image path (for validation)
     */
    Optional<BikeImage> findByImages(String imagePath);

    /**
     * Find bike images created after a specific timestamp
     */
    @Query("SELECT bi FROM BikeImage bi WHERE bi.bike.id = :bikeId AND bi.createdAt > :timestamp")
    List<BikeImage> findByBikeIdAndCreatedAfter(@Param("bikeId") Integer bikeId,
                                                @Param("timestamp") Timestamp timestamp);

    /**
     * Find bike images created between two timestamps
     */
    @Query("SELECT bi FROM BikeImage bi WHERE bi.bike.id = :bikeId AND bi.createdAt BETWEEN :startDate AND :endDate")
    List<BikeImage> findByBikeIdAndCreatedBetween(@Param("bikeId") Integer bikeId,
                                                  @Param("startDate") Timestamp startDate,
                                                  @Param("endDate") Timestamp endDate);

    /**
     * Get latest bike images (most recent first)
     */
    @Query("SELECT bi FROM BikeImage bi WHERE bi.bike.id = :bikeId ORDER BY bi.createdAt DESC")
    List<BikeImage> findLatestByBikeId(@Param("bikeId") Integer bikeId, Pageable pageable);

    /**
     * Find all bike images for multiple bike IDs
     */
    @Query("SELECT bi FROM BikeImage bi WHERE bi.bike.id IN :bikeIds")
    List<BikeImage> findByBikeIds(@Param("bikeIds") List<Integer> bikeIds);

    /**
     * Delete bike images older than specified timestamp
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM BikeImage bi WHERE bi.bike.id = :bikeId AND bi.createdAt < :timestamp")
    void deleteByBikeIdAndCreatedBefore(@Param("bikeId") Integer bikeId,
                                        @Param("timestamp") Timestamp timestamp);

    /**
     * Update bike image path
     */
    @Modifying
    @Transactional
    @Query("UPDATE BikeImage bi SET bi.images = :newImagePath, bi.updatedAt = :updatedAt WHERE bi.id = :id")
    void updateImagePath(@Param("id") Long id,
                         @Param("newImagePath") String newImagePath,
                         @Param("updatedAt") Timestamp updatedAt);

    /**
     * Find bike images by partial image path (for searching)
     */
    @Query("SELECT bi FROM BikeImage bi WHERE bi.images LIKE %:pathPattern%")
    List<BikeImage> findByImagePathContaining(@Param("pathPattern") String pathPattern);

    /**
     * Get total image count for a bike
     */
    @Query("SELECT COUNT(bi) FROM BikeImage bi WHERE bi.bike.id = :bikeId")
    Long getTotalImageCountForBike(@Param("bikeId") Integer bikeId);

    /**
     * Find orphaned bike images (images without associated bike)
     */
    @Query("SELECT bi FROM BikeImage bi WHERE bi.bike IS NULL")
    List<BikeImage> findOrphanedImages();

    /**
     * Delete orphaned bike images
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM BikeImage bi WHERE bi.bike IS NULL")
    void deleteOrphanedImages();

    /**
     * Find bike images by bike brand (through bike relationship)
     */
    @Query("SELECT bi FROM BikeImage bi WHERE bi.bike.brandId = :brandId")
    List<BikeImage> findByBikeBrandId(@Param("brandId") Integer brandId);

    /**
     * Get recent bike images across all bikes
     */
    @Query("SELECT bi FROM BikeImage bi ORDER BY bi.createdAt DESC")
    List<BikeImage> findRecentImages(Pageable pageable);

    /**
     * Batch delete bike images by IDs
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM BikeImage bi WHERE bi.id IN :ids")
    void deleteByIds(@Param("ids") List<Long> ids);

    /**
     * Find duplicate image paths (for cleanup)
     */
    @Query("SELECT bi.images FROM BikeImage bi GROUP BY bi.images HAVING COUNT(bi.images) > 1")
    List<String> findDuplicateImagePaths();


    // In BikeImageRepository
    @Query("select bi.images from BikeImage bi where bi.bike.id = :bikeId order by bi.createdAt desc, bi.id desc")
    List<String> findImagePathsByBikeId(@Param("bikeId") Integer bikeId);

}
