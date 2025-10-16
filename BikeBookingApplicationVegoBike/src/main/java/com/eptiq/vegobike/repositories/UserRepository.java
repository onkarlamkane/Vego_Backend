
package com.eptiq.vegobike.repositories;

import com.eptiq.vegobike.model.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Find by email
    Optional<User> findByEmail(String email);

    // Find by phone number
    Optional<User> findByPhoneNumber(String phoneNumber);

    // Check if phone exists
    boolean existsByPhoneNumber(String phoneNumber);

    // Check if email exists
    boolean existsByEmail(String email);

    // Role-based search (by roleId instead of Role object)
    Page<User> findByRoleId(Integer roleId, Pageable pageable);

    List<User> findByRoleId(Integer roleId);

    @Query("""
        SELECT u FROM User u 
        WHERE u.roleId = 3
          AND (
              LOWER(u.name) LIKE LOWER(CONCAT('%', :searchText, '%')) OR
              LOWER(u.email) LIKE LOWER(CONCAT('%', :searchText, '%')) OR
              LOWER(u.phoneNumber) LIKE LOWER(CONCAT('%', :searchText, '%'))
          )
    """)
    List<User> searchUsersByText(@Param("searchText") String searchText);

}

