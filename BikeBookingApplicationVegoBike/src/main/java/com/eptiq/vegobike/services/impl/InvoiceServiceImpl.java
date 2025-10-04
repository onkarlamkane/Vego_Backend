package com.eptiq.vegobike.services.impl;

import com.eptiq.vegobike.dtos.InvoiceDto;
import com.eptiq.vegobike.exceptions.NotFoundException;
import com.eptiq.vegobike.mappers.InvoiceMapper;
import com.eptiq.vegobike.model.Invoice;
import com.eptiq.vegobike.services.InvoiceService;
import com.eptiq.vegobike.repositories.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    InvoiceRepository invoiceRepository;
    private final InvoiceMapper invoiceMapper; // Inject mapper properly

    @Override
    public InvoiceDto getInvoiceByBookingId(int bookingId) {
        Invoice invoice = invoiceRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new NotFoundException("Invoice not found for booking ID: " + bookingId));
        return invoiceMapper.toDTO(invoice);
    }
}
