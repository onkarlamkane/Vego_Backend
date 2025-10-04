package com.eptiq.vegobike.repositories;

import com.eptiq.vegobike.model.BookingRequest;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BookingRequestRepository extends JpaRepository<BookingRequest, Integer> {

    Optional<BookingRequest> findByBookingId(String bookingId);

    Page<BookingRequest> findByCustomerId(int customerId, Pageable pageable);


    boolean existsByCustomerIdAndBookingStatusNotIn(int customerId, List<Integer> statuses);

    /**
     * Get active bookings for a customer (not completed or cancelled)
     * @param customerId Customer ID
     * @param statuses Status IDs to exclude
     * @return List of active booking requests
     */
    List<BookingRequest> findByCustomerIdAndBookingStatusNotIn(int customerId, List<Integer> statuses);

    /**
     * Count active bookings for a customer
     * @param customerId Customer ID
     * @param statuses Status IDs to exclude
     * @return Count of active bookings
     */
    long countByCustomerIdAndBookingStatusNotIn(int customerId, List<Integer> statuses);

    /**
     * Get the most recent active booking for a customer
     * @param customerId Customer ID
     * @param statuses Status IDs to exclude
     * @return Optional of the most recent active booking
     */
    @Query("SELECT br FROM BookingRequest br WHERE br.customerId = :customerId " +
            "AND br.bookingStatus NOT IN :statuses " +
            "ORDER BY br.createdAt DESC")
    Optional<BookingRequest> findMostRecentActiveBooking(@Param("customerId") int customerId,
                                                         @Param("statuses") List<Integer> statuses);

    /**
     * Check if customer has any bookings with specific status
     * @param customerId Customer ID
     * @param status Booking status ID
     * @return true if customer has bookings with the specified status
     */
    boolean existsByCustomerIdAndBookingStatus(int customerId, int status);

    /**
     * Get bookings by customer and status
     * @param customerId Customer ID
     * @param status Booking status ID
     * @param pageable Pagination information
     * @return Page of booking requests
     */
    Page<BookingRequest> findByCustomerIdAndBookingStatus(int customerId, int status, Pageable pageable);

    /**
     * Get all bookings for customer ordered by creation date (most recent first)
     * @param customerId Customer ID
     * @param pageable Pagination information
     * @return Page of booking requests ordered by created date desc
     */
    @Query("SELECT br FROM BookingRequest br WHERE br.customerId = :customerId ORDER BY br.createdAt DESC")
    Page<BookingRequest> findByCustomerIdOrderByCreatedAtDesc(@Param("customerId") int customerId, Pageable pageable);

    /**
     * Get booking statistics for a customer
     * @param customerId Customer ID
     * @return Array containing [total_bookings, completed_bookings, cancelled_bookings, active_bookings]
     */
    @Query("SELECT COUNT(br), " +
            "SUM(CASE WHEN br.bookingStatus = 5 THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN br.bookingStatus = 7 THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN br.bookingStatus NOT IN (5, 7) THEN 1 ELSE 0 END) " +
            "FROM BookingRequest br WHERE br.customerId = :customerId")
    Object[] getBookingStatsByCustomerId(@Param("customerId") int customerId);



}
