package com.eptiq.vegobike.repositories;

import com.eptiq.vegobike.model.UserDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserDocumentRepository extends JpaRepository<UserDocument, Integer> {
    Optional<UserDocument> findByUserId(Integer userId);
    Optional<UserDocument> findByUserIdAndIsActive(Integer userId, int isActive);
}
