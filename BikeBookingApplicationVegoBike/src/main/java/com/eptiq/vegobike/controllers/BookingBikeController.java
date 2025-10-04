package com.eptiq.vegobike.controllers;

import com.eptiq.vegobike.dtos.BookingBikeResponse;
import com.eptiq.vegobike.dtos.BookingRequestDto;
import com.eptiq.vegobike.dtos.InvoiceDto;
import com.eptiq.vegobike.exceptions.ActiveBookingExistsException;
import com.eptiq.vegobike.exceptions.DocumentVerificationException;
import com.eptiq.vegobike.exceptions.ResourceNotFoundException;
import com.eptiq.vegobike.exceptions.UnauthorizedException;
import com.eptiq.vegobike.services.BookingBikeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/booking-bikes")
@RequiredArgsConstructor
@Slf4j
public class BookingBikeController {

    private final BookingBikeService service;

//    @PostMapping("/create")
//    public ResponseEntity<BookingBikeResponse> create(@RequestBody BookingRequestDto request) {
//        return ResponseEntity.ok(service.createBookingBike(request));
//    }

    @PostMapping("/create")
    public ResponseEntity<?> create(
            @RequestBody @Valid BookingRequestDto request,
            HttpServletRequest httpRequest) {
        try {
            log.info("📋 Creating new booking request for vehicle ID: {}", request.getVehicleId());

            BookingBikeResponse response = service.createBookingBike(request, httpRequest);

            log.info("✅ Booking created successfully - Booking ID: {}, Status: {}",
                    response.getBookingId(), response.getStatus());

            return ResponseEntity.ok(response);

        } catch (ActiveBookingExistsException e) {
            log.warn("📋 Active booking exists: {}", e.getMessage());

            Map<String, Object> errorResponse = Map.of(
                    "status", "ACTIVE_BOOKING_EXISTS",
                    "errorCode", "BOOKING_001",
                    "timestamp", System.currentTimeMillis()
            );
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);

        } catch (DocumentVerificationException e) {
            log.warn("📄 Document verification failed: {}", e.getMessage());

            Map<String, Object> errorResponse = Map.of(
                    "status", "DOCUMENT_VERIFICATION_FAILED",
                    "message", e.getMessage(),
                    "errorCode", "BOOKING_002",
                    "timestamp", System.currentTimeMillis()
            );
            return ResponseEntity.badRequest().body(errorResponse);

        } catch (IllegalArgumentException e) {
            log.error("📋 Invalid request data: {}", e.getMessage());

            Map<String, Object> errorResponse = Map.of(
                    "status", "INVALID_REQUEST",
                    "message", "Invalid request data: " + e.getMessage(),
                    "errorCode", "BOOKING_003",
                    "timestamp", System.currentTimeMillis()
            );
            return ResponseEntity.badRequest().body(errorResponse);

        } catch (Exception e) {
            log.error("💥 Unexpected error during booking creation: {}", e.getMessage(), e);

            Map<String, Object> errorResponse = Map.of(
                    "status", "BOOKING_FAILED",
                    "message", "Failed to create booking. Please try again later.",
                    "errorCode", "BOOKING_999",
                    "timestamp", System.currentTimeMillis()
            );
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }



    @GetMapping("/allBooking")
    public ResponseEntity<List<BookingBikeResponse>> getAll() {
        return ResponseEntity.ok(service.getAllBookingBikes());
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<BookingBikeResponse> getById(@PathVariable int id) {
        return ResponseEntity.ok(service.getBookingBikeById(id));
    }

    @PostMapping("/{bookingId}/accept")
    public ResponseEntity<?> acceptBooking(
            @PathVariable int bookingId,  // ✅ Changed from String to int
            HttpServletRequest httpRequest) {
        try {
            log.info("📋 Processing booking acceptance request for: {}", bookingId);

            BookingBikeResponse response = service.acceptBooking(bookingId);  // ✅ Now passes int

            log.info("✅ Booking accepted successfully: {}", bookingId);
            return ResponseEntity.ok(response);

        } catch (UnauthorizedException e) {
            log.warn("🔐 Unauthorized booking acceptance attempt for {}: {}", bookingId, e.getMessage());

            Map<String, Object> errorResponse = Map.of(
                    "status", "UNAUTHORIZED",
                    "errorCode", "ACCEPT_000",
                    "bookingId", bookingId,
                    "timestamp", System.currentTimeMillis()
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);

        } catch (DocumentVerificationException e) {
            log.warn("📄 Document verification failed for booking {}: {}", bookingId, e.getMessage());

            Map<String, Object> errorResponse = Map.of(
                    "status", "DOCUMENT_VERIFICATION_REQUIRED",
                    "message", e.getMessage(),
                    "errorCode", "ACCEPT_001",
                    "bookingId", bookingId,
                    "timestamp", System.currentTimeMillis()
            );
            return ResponseEntity.badRequest().body(errorResponse);

        } catch (IllegalStateException e) {
            log.warn("⚠ Invalid booking state for {}: {}", bookingId, e.getMessage());

            Map<String, Object> errorResponse = Map.of(
                    "status", "INVALID_BOOKING_STATE",
                    "message", e.getMessage(),
                    "errorCode", "ACCEPT_002",
                    "bookingId", bookingId,
                    "timestamp", System.currentTimeMillis()
            );
            return ResponseEntity.badRequest().body(errorResponse);

        } catch (ResourceNotFoundException e) {
            log.warn("📋 Booking not found: {}", bookingId);

            Map<String, Object> errorResponse = Map.of(
                    "status", "BOOKING_NOT_FOUND",
                    "message", "Booking not found with ID: " + bookingId,
                    "errorCode", "ACCEPT_003",
                    "bookingId", bookingId,
                    "timestamp", System.currentTimeMillis()
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);  // ✅ Fixed to return body

        } catch (Exception e) {
            log.error("💥 Unexpected error accepting booking {}: {}", bookingId, e.getMessage(), e);

            Map<String, Object> errorResponse = Map.of(
                    "status", "ACCEPTANCE_FAILED",
                    "message", "Failed to accept booking. Please try again.",
                    "errorCode", "ACCEPT_999",
                    "bookingId", bookingId,
                    "timestamp", System.currentTimeMillis()
            );
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @PostMapping(value = "/{bookingId}/start", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> startTrip(
            @PathVariable String bookingId,
            @RequestPart(value = "images") MultipartFile[] images,
            HttpServletRequest httpRequest) {
        try {
            log.info("📋 Processing trip start request for booking: {}", bookingId);

            // Validate images count
            if (images == null || images.length != 4) {
                log.warn("⚠ Invalid number of images for trip start - Expected: 4, Received: {}",
                        images != null ? images.length : 0);

                Map<String, Object> errorResponse = Map.of(
                        "status", "INVALID_REQUEST",
                        "message", "Exactly 4 bike images are required to start trip",
                        "errorCode", "START_001",
                        "bookingId", bookingId,
                        "imagesReceived", images != null ? images.length : 0,
                        "imagesRequired", 4,
                        "timestamp", System.currentTimeMillis()
                );
                return ResponseEntity.badRequest().body(errorResponse);
            }

            BookingBikeResponse response = service.startTrip(bookingId, images, httpRequest);

            log.info("✅ Trip started successfully for booking: {}", bookingId);
            return ResponseEntity.ok(response);

        } catch (UnauthorizedException e) {
            log.warn("🔐 Unauthorized trip start attempt for {}: {}", bookingId, e.getMessage());

            Map<String, Object> errorResponse = Map.of(
                    "status", "UNAUTHORIZED",
                    "message", e.getMessage(),
                    "errorCode", "START_000",
                    "bookingId", bookingId,
                    "timestamp", System.currentTimeMillis()
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);

        } catch (IllegalStateException e) {
            log.warn("⚠ Invalid booking state for trip start {}: {}", bookingId, e.getMessage());

            Map<String, Object> errorResponse = Map.of(
                    "status", "INVALID_BOOKING_STATE",
                    "message", e.getMessage(),
                    "errorCode", "START_002",
                    "bookingId", bookingId,
                    "timestamp", System.currentTimeMillis()
            );
            return ResponseEntity.badRequest().body(errorResponse);

        } catch (ResourceNotFoundException e) {
            log.warn("📋 Booking not found: {}", bookingId);

            Map<String, Object> errorResponse = Map.of(
                    "status", "BOOKING_NOT_FOUND",
                    "message", "Booking not found with ID: " + bookingId,
                    "errorCode", "START_003",
                    "bookingId", bookingId,
                    "timestamp", System.currentTimeMillis()
            );
            return ResponseEntity.notFound().build();

        } catch (Exception e) {
            log.error("💥 Unexpected error starting trip for booking {}: {}", bookingId, e.getMessage());

            Map<String, Object> errorResponse = Map.of(
                    "status", "TRIP_START_FAILED",
                    "message", "Failed to start trip. Please try again.",
                    "errorCode", "START_999",
                    "bookingId", bookingId,
                    "timestamp", System.currentTimeMillis()
            );
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @PostMapping(value = "/{bookingId}/end", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> endTrip(
            @PathVariable String bookingId,
            @RequestPart(value = "images") MultipartFile[] images,
            HttpServletRequest httpRequest) {
        try {
            log.info("📋 Processing trip end request for booking: {}", bookingId);

            // Validate images count
            if (images == null || images.length != 4) {
                log.warn("⚠ Invalid number of images for trip end - Expected: 4, Received: {}",
                        images != null ? images.length : 0);

                Map<String, Object> errorResponse = Map.of(
                        "status", "INVALID_REQUEST",
                        "message", "Exactly 4 bike images are required to end trip",
                        "errorCode", "END_001",
                        "bookingId", bookingId,
                        "imagesReceived", images != null ? images.length : 0,
                        "imagesRequired", 4,
                        "timestamp", System.currentTimeMillis()
                );
                return ResponseEntity.badRequest().body(errorResponse);
            }

            BookingBikeResponse response = service.endTrip(bookingId, images, httpRequest);

            log.info("✅ Trip ended successfully for booking: {}", bookingId);
            return ResponseEntity.ok(response);

        } catch (UnauthorizedException e) {
            log.warn("🔐 Unauthorized trip end attempt for {}: {}", bookingId, e.getMessage());

            Map<String, Object> errorResponse = Map.of(
                    "status", "UNAUTHORIZED",
                    "message", e.getMessage(),
                    "errorCode", "END_000",
                    "bookingId", bookingId,
                    "timestamp", System.currentTimeMillis()
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);

        } catch (IllegalStateException e) {
            log.warn("⚠ Invalid booking state for trip end {}: {}", bookingId, e.getMessage());

            Map<String, Object> errorResponse = Map.of(
                    "status", "INVALID_BOOKING_STATE",
                    "message", e.getMessage(),
                    "errorCode", "END_002",
                    "bookingId", bookingId,
                    "timestamp", System.currentTimeMillis()
            );
            return ResponseEntity.badRequest().body(errorResponse);

        } catch (ResourceNotFoundException e) {
            log.warn("📋 Booking not found: {}", bookingId);

            Map<String, Object> errorResponse = Map.of(
                    "status", "BOOKING_NOT_FOUND",
                    "message", "Booking not found with ID: " + bookingId,
                    "errorCode", "END_003",
                    "bookingId", bookingId,
                    "timestamp", System.currentTimeMillis()
            );
            return ResponseEntity.notFound().build();

        } catch (Exception e) {
            log.error("💥 Unexpected error ending trip for booking {}: {}", bookingId, e.getMessage());

            Map<String, Object> errorResponse = Map.of(
                    "status", "TRIP_END_FAILED",
                    "message", "Failed to end trip. Please try again.",
                    "errorCode", "END_999",
                    "bookingId", bookingId,
                    "timestamp", System.currentTimeMillis()
            );
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @PostMapping("/{bookingId}/complete")
    public ResponseEntity<?> completeBooking(
            @PathVariable int bookingId,
            HttpServletRequest httpRequest) {
        try {
            log.info("🏁 Processing booking completion request for: {}", bookingId);

            // ✅ Now returns InvoiceDto
            InvoiceDto invoice = service.completeBooking(bookingId);

            log.info("✅ Booking completed successfully and invoice generated: {}", bookingId);

            // Return invoice in response
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Booking completed successfully",
                    "invoice", invoice
            ));

        } catch (ResourceNotFoundException e) {
            log.warn("📋 Booking not found: {}", bookingId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", "BOOKING_NOT_FOUND",
                    "message", "Booking not found with ID: " + bookingId,
                    "errorCode", "COMPLETE_001",
                    "timestamp", System.currentTimeMillis()
            ));

        } catch (Exception e) {
            log.error("💥 Error completing booking {}: {}", bookingId, e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "COMPLETION_FAILED",
                    "message", "Failed to complete booking",
                    "errorCode", "COMPLETE_999",
                    "timestamp", System.currentTimeMillis()
            ));
        }
    }

    @PostMapping("/{bookingId}/cancel")
    public ResponseEntity<?> cancelBooking(
            @PathVariable int bookingId,  // ✅ Changed from String to int
            HttpServletRequest httpRequest) {
        try {
            log.info("❌ Processing booking cancellation request for: {}", bookingId);

            BookingBikeResponse response = service.cancelBooking(bookingId);

            log.info("✅ Booking cancelled successfully: {}", bookingId);
            return ResponseEntity.ok(response);

        } catch (ResourceNotFoundException e) {
            log.warn("📋 Booking not found: {}", bookingId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", "BOOKING_NOT_FOUND",
                    "message", "Booking not found with ID: " + bookingId,
                    "errorCode", "CANCEL_001",
                    "timestamp", System.currentTimeMillis()
            ));

        } catch (Exception e) {
            log.error("💥 Error cancelling booking {}: {}", bookingId, e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "CANCELLATION_FAILED",
                    "message", "Failed to cancel booking",
                    "errorCode", "CANCEL_999",
                    "timestamp", System.currentTimeMillis()
            ));
        }
    }

    @GetMapping("/by-customer")
    public ResponseEntity<List<BookingBikeResponse>> getBookingsByCustomer(
            @RequestParam int customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "latest") String sortBy) {
        return ResponseEntity.ok(service.getBookingsByCustomerWithOptions(customerId, page, size, sortBy));
    }
}