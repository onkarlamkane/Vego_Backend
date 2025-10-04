package com.eptiq.vegobike.services.impl;

import com.eptiq.vegobike.model.BookingStatus;
import com.eptiq.vegobike.repositories.BookingStatusRepository;
import com.eptiq.vegobike.services.BookingStatusService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingStatusServiceImpl implements BookingStatusService {

    private final BookingStatusRepository repository;

    public BookingStatusServiceImpl(BookingStatusRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<BookingStatus> getAllStatuses() {
        return repository.findAll();
    }

    @Override
    public BookingStatus getStatusById(int id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking Status not found with id: " + id));
    }
}
