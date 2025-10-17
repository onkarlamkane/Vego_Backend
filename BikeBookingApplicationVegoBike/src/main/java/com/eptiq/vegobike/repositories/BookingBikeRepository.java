package com.eptiq.vegobike.repositories;

import com.eptiq.vegobike.model.BookingBike;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookingBikeRepository extends JpaRepository<BookingBike, Integer> {

    @Query("SELECT b.images FROM BookingBike b WHERE b.bookingId = :bookingId AND b.type = :type")
    List<String> findImagesByBookingAndType(@Param("bookingId") int bookingId, @Param("type") int type);




}
