package com.eptiq.vegobike.services;

import com.eptiq.vegobike.dtos.BookingBikeResponse;
import com.eptiq.vegobike.dtos.BookingRequestDto;
import com.eptiq.vegobike.dtos.InvoiceDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BookingBikeService {

    BookingBikeResponse createBookingBike(BookingRequestDto request, HttpServletRequest httpRequest);

    List<BookingBikeResponse> getAllBookingBikes();

    BookingBikeResponse getBookingBikeById(int id);

    BookingBikeResponse acceptBooking(int bookingId);

    BookingBikeResponse startTrip(String bookingId, MultipartFile[] images, HttpServletRequest httpRequest);

    BookingBikeResponse endTrip(String bookingId, MultipartFile[] images, HttpServletRequest httpRequest);
    InvoiceDto completeBooking(int bookingId);

    BookingBikeResponse cancelBooking(int bookingId);

    void addAdditionalCharges(String bookingId, Float charges, String details);

    List<BookingBikeResponse> getBookingsByCustomerWithOptions(int customerId, int page, int size, String sortBy);

}