package com.eptiq.vegobike.services;

import com.eptiq.vegobike.dtos.AvailableBikeRow;
import com.eptiq.vegobike.dtos.BookingBikeResponse;
import com.eptiq.vegobike.dtos.BookingRequestDto;
import com.eptiq.vegobike.dtos.InvoiceDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BookingBikeService {

    BookingBikeResponse createBookingBike(BookingRequestDto request, HttpServletRequest httpRequest);

    Page<BookingBikeResponse> getAllBookingBikes(Pageable pageable);
    BookingBikeResponse getBookingBikeById(int id);

    BookingBikeResponse acceptBooking(int bookingId);

    BookingBikeResponse startTrip(String bookingId, MultipartFile[] images, Double startTripKm, HttpServletRequest httpRequest);

    BookingBikeResponse endTrip(String bookingId, MultipartFile[] images, Double endTrip , HttpServletRequest httpRequest);

    InvoiceDto completeBooking(int bookingId, Double endTripKm);
    String shouldPromptEndTripKm(int bookingId);

    BookingBikeResponse cancelBooking(int bookingId);

    void addAdditionalCharges(String bookingId, Float charges, String details);

    List<BookingBikeResponse> getBookingsByCustomerWithOptions(int customerId, int page, int size, String sortBy);

    void updateOnlinePayment(String orderId, String paymentId, String signature) throws Exception;

    BookingBikeResponse createBookingByAdmin(BookingRequestDto bookingRequestDto);

    BookingBikeResponse exchangeBike(int bookingId, int newBikeId);

    List<AvailableBikeRow> getAvailableBikesForExchangeCategory(int bookingId);

    List<BookingBikeResponse> searchBookingBikes(String searchText);




}