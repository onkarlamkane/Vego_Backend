package com.eptiq.vegobike.controllers;

import com.eptiq.vegobike.model.BookingStatus;
import com.eptiq.vegobike.services.BookingStatusService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/booking-statuses")
public class BookingStatusController {

    private final BookingStatusService service;

    public BookingStatusController(BookingStatusService service) {
        this.service = service;
    }

    @GetMapping
    public List<BookingStatus> getAllStatuses() {
        return service.getAllStatuses();
    }

    @GetMapping("/{id}")
    public BookingStatus getStatus(@PathVariable int id) {
        return service.getStatusById(id);
    }
}
