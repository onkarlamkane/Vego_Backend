package com.eptiq.vegobike.repositories;

import com.eptiq.vegobike.model.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BookingStatusRepository extends JpaRepository<BookingStatus, Integer> {

    /**
     * Find booking status name by ID
     * @param statusId Status ID
     * @return Status name if found
     */
    @Query("SELECT bs.name FROM BookingStatus bs WHERE bs.id = :statusId")
    Optional<String> findNameById(@Param("statusId") int statusId);
}
