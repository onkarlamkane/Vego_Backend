
package com.eptiq.vegobike.repositories;

import com.eptiq.vegobike.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
}

