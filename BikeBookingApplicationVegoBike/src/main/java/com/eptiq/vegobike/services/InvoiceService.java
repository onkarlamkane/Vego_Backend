package com.eptiq.vegobike.services;

import com.eptiq.vegobike.dtos.InvoiceDto;

public interface InvoiceService {
    InvoiceDto getInvoiceByBookingId(int bookingId);
}
