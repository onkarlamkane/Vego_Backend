package com.eptiq.vegobike.repositories;

import com.eptiq.vegobike.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {
    Optional<Invoice> findByBookingId(int bookingId);
}
