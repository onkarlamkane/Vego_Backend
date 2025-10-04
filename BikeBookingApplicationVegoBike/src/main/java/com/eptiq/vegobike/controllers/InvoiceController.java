package com.eptiq.vegobike.controllers;

import com.eptiq.vegobike.dtos.InvoiceDto;
import com.eptiq.vegobike.services.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping("/{bookingId}")
    public ResponseEntity<InvoiceDto> getInvoice(@PathVariable int bookingId) {
        return ResponseEntity.ok(invoiceService.getInvoiceByBookingId(bookingId));
    }
}
