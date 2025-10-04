//package com.eptiq.vegobike.repositories;
//
//import com.eptiq.vegobike.model.Model;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.List;
//import java.util.Optional;
//
//public interface ModelRepository extends JpaRepository<Model, Integer> {
//
//    // Search models by name
//    Page<Model> findByModelNameContainingIgnoreCase(String modelName, Pageable pageable);
//
//    // Find model by id ignoring @Where/status (if you use soft delete)
//    Optional<Model> findById(Integer id);
//
//    // Active models only
//    //List<Model> findByIsActive(Integer isActive);
//
//    List<Model> findByIsActiveEquals(Integer isActive);
//
//
//  List<Model> findByBrandIdAndIsActiveTrue(Integer brandId);
//
//}

package com.eptiq.vegobike.repositories;

import com.eptiq.vegobike.model.Model;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ModelRepository extends JpaRepository<Model, Integer> {

    // Search models by name
    Page<Model> findByModelNameContainingIgnoreCase(String modelName, Pageable pageable);

    // Find model by id ignoring @Where/status (if you use soft delete)
    Optional<Model> findById(Integer id);

    // Active models only  (store isActive as integer 0/1 in DB)
    List<Model> findByIsActiveEquals(Integer isActive);

    // Active models for a brand (works with integer 0/1 column)
    List<Model> findByBrandIdAndIsActiveEquals(Integer brandId, Integer isActive);
}