package com.eptiq.vegobike.repositories;

import com.eptiq.vegobike.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    // Find categories by status
    Page<Category> findByIsActive(Integer isActive, Pageable pageable);

    // Find by category name (case insensitive)
    Optional<Category> findByCategoryNameIgnoreCase(String categoryName);

    // Check if category name exists (for validation)
    boolean existsByCategoryNameIgnoreCase(String categoryName);

    // Custom query for active categories
    @Query("SELECT c FROM Category c WHERE c.isActive = 1 ORDER BY c.categoryName")
    List<Category> findAllActiveCategories();

    // Search categories by name
    @Query("SELECT c FROM Category c WHERE c.categoryName LIKE %:name% ORDER BY c.createdAt DESC")
    Page<Category> searchByName(@Param("name") String name, Pageable pageable);

    // Count categories by status
    @Query("SELECT COUNT(c) FROM Category c WHERE c.isActive = :status")
    long countByStatus(@Param("status") Integer status);
}
