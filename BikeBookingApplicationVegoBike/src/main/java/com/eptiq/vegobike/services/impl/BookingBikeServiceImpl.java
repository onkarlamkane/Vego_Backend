package com.eptiq.vegobike.services.impl;

import com.eptiq.vegobike.dtos.BookingBikeResponse;
import com.eptiq.vegobike.dtos.BookingRequestDto;
import com.eptiq.vegobike.dtos.InvoiceDto;
import com.eptiq.vegobike.enums.VerificationStatus;
import com.eptiq.vegobike.exceptions.*;
import com.eptiq.vegobike.mappers.BookingBikeMapper;
import com.eptiq.vegobike.mappers.BookingRequestMapper;
import com.eptiq.vegobike.model.Bike;
import com.eptiq.vegobike.model.BookingBike;
import com.eptiq.vegobike.model.BookingRequest;
import com.eptiq.vegobike.repositories.*;
import com.eptiq.vegobike.services.BookingBikeService;
import com.eptiq.vegobike.services.JwtService;
import com.eptiq.vegobike.services.RazorpayService;
import com.eptiq.vegobike.services.UserDocumentService;
import com.eptiq.vegobike.utils.ImageUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BookingBikeServiceImpl implements BookingBikeService {

    private final BookingBikeRepository repository;
    private final BookingBikeMapper mapper;
    private final BikeRepository bikeRepository;
    private final BookingRequestRepository bookingRequestRepository;
    private final BookingRequestMapper bookingRequestMapper;
    private final InvoiceRepository invoiceRepository;
    private final ImageUtils imageUtils;
    private final JwtService jwtService;
    private final UserDocumentService userDocumentService;
    private final BookingStatusRepository bookingStatusRepository;
    private final RazorpayService razorpayService;


    @Override
    public BookingBikeResponse createBookingBike(BookingRequestDto request, HttpServletRequest httpRequest) {
        Long customerId = extractCustomerIdFromToken(httpRequest);

        // Check active bookings & document status
        checkActiveBookings(customerId.intValue());
        boolean hasAllDocsUploaded = userDocumentService.hasAllDocumentsUploaded(customerId.intValue());
        VerificationStatus docStatus = userDocumentService.checkDocumentsForBooking(customerId.intValue());

        validateBookingRequest(request);

        // Map DTO to Entity
        BookingRequest entity = mapper.toBookingRequestEntity(request);
        entity.setCustomerId(customerId.intValue());
        entity.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        entity.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        entity.setBookingStatus(1);

        // ✅ RAZORPAY LOGIC - Create order if payment type is Online (2)
        String razorpayOrderDetails = null;
        log.info("🔍 Payment Type: {}", entity.getPaymentType());

        if (entity.getPaymentType() == 2) { // Online payment
            // Online payment
            double amount = entity.getFinalAmount();
            String currency = "INR";

            try {
                log.info("💳 Creating Razorpay order for amount: {} INR", amount);
                String razorpayOrderJson = razorpayService.createOrder(amount, currency);
                log.info("✅ Razorpay order created: {}", razorpayOrderJson);

                org.json.JSONObject razorpayOrder = new org.json.JSONObject(razorpayOrderJson);
                String razorpayOrderId = razorpayOrder.getString("id");

                entity.setMerchantTransactionId(razorpayOrderId);
                entity.setPaymentStatus("INITIATED");
                razorpayOrderDetails = razorpayOrderJson;

                log.info("✅ Razorpay Order ID: {}", razorpayOrderId);

            } catch (Exception ex) {
                log.error("❌ Failed to create Razorpay order: {}", ex.getMessage(), ex);
                entity.setPaymentStatus("FAILED_INITIATION");
                throw new RuntimeException("Failed to initialize payment gateway: " + ex.getMessage());
            }
        } else {
            log.info("💵 Payment Type is COD (1)");
            entity.setPaymentStatus("PENDING");
        }

        setDefaultValues(entity, request);
        BookingRequest savedEntity = bookingRequestRepository.save(entity);

        // Generate booking ID if not present
        if (savedEntity.getBookingId() == null || savedEntity.getBookingId().isEmpty()) {
            String generatedBookingId = String.format("VEGO%03d", savedEntity.getId());
            savedEntity.setBookingId(generatedBookingId);
            savedEntity = bookingRequestRepository.save(savedEntity);
        }

        Bike bike = bikeRepository.findById(savedEntity.getVehicleId()).orElse(null);
        BookingBikeResponse response = mapper.toResponse(savedEntity, bike);

        // ✅ CRITICAL: Set Razorpay details in response
        response.setMerchantTransactionId(savedEntity.getMerchantTransactionId());
        response.setPaymentStatus(savedEntity.getPaymentStatus());
        response.setPaymentType(savedEntity.getPaymentType());

        if (entity.getPaymentType() == 2) {
            // ✅ Set Razorpay order details for frontend
            response.setRazorpayOrderDetails(razorpayOrderDetails);
            response.setMessage("Booking created successfully. Please complete the online payment.");

            log.info("✅ Response includes Razorpay order details");
            log.debug("📦 Razorpay Order JSON: {}", razorpayOrderDetails);
        } else {
            // COD message logic
            if (!hasAllDocsUploaded) {
                response.setMessage("Booking created successfully. Please upload your Aadhaar front, Aadhaar back, and Driving License documents.");
            } else if (docStatus == VerificationStatus.PENDING) {
                response.setMessage("Booking created successfully. Your documents are under verification.");
            } else if (docStatus == VerificationStatus.REJECTED) {
                response.setMessage("Booking created successfully. Your documents were rejected. Please update your documents.");
            } else if (docStatus == VerificationStatus.VERIFIED) {
                response.setMessage("Booking created successfully. All your documents are verified.");
            } else {
                response.setMessage("Booking created successfully.");
            }
        }

        log.info("✅ Final response - Booking ID: {}, Payment Type: {}, Merchant TX ID: {}",
                response.getBookingId(), response.getPaymentType(), response.getMerchantTransactionId());

        return response;
    }


    private void checkActiveBookings(int customerId) {
        try {
            log.debug("🔍 Checking active bookings for customer ID: {}", customerId);

            // Define completed and cancelled status IDs based on your booking_statuses table
            List<Integer> finalStatuses = Arrays.asList(5, 7); // 5=Completed, 7=Cancelled

            // Check if customer has any bookings that are not in final states
            boolean hasActiveBooking = bookingRequestRepository.existsByCustomerIdAndBookingStatusNotIn(
                    customerId, finalStatuses);

            if (hasActiveBooking) {
                // Get count for logging purposes
                long activeBookingCount = bookingRequestRepository.countByCustomerIdAndBookingStatusNotIn(
                        customerId, finalStatuses);

                log.warn("⚠ Customer {} has {} active booking(s), blocking new booking creation",
                        customerId, activeBookingCount);

                // Get the most recent active booking for additional context
                Optional<BookingRequest> recentBooking = bookingRequestRepository
                        .findMostRecentActiveBooking(customerId, finalStatuses);

                String message = "You have an ongoing booking. Please complete or cancel your current booking before creating a new one.";
                if (recentBooking.isPresent()) {
                    message += String.format(" Current booking ID: %s", recentBooking.get().getBookingId());
                }

                throw new ActiveBookingExistsException(message);
            }

            log.debug("✅ No active bookings found for customer {}", customerId);

        } catch (ActiveBookingExistsException e) {
            throw e; // Re-throw our custom exception
        } catch (Exception e) {
            log.error("💥 Error checking active bookings for customer {}: {}", customerId, e.getMessage());
            throw new RuntimeException("Unable to verify booking status. Please try again.");
        }
    }

    private Long extractCustomerIdFromToken(HttpServletRequest request) {
        try {
            // Use the JWT service method to extract customer ID from request
            Long customerId = jwtService.extractCustomerIdFromRequest(request);

            log.debug("🔐 BOOKING_CUSTOMER_ID_EXTRACTED - CustomerID: {}", customerId);
            return customerId;

        } catch (Exception e) {
            log.error("🚫 BOOKING_CUSTOMER_ID_EXTRACTION_FAILED - Error: {}", e.getMessage());
            throw new RuntimeException("Authentication failed: " + e.getMessage());
        }
    }


    // Method to check document verification status for all three documents


    // Method to set appropriate booking message based on document status
    private void setBookingMessage(BookingBikeResponse response, VerificationStatus docResult) {
        switch (docResult) {
            case VERIFIED:

                response.setStatus("CONFIRMED");
                log.info("✅ Booking confirmed with verified documents");
                break;
            case PENDING:

                response.setStatus("CONFIRMED_PENDING_VERIFICATION");
                log.info("⏳ Booking confirmed with pending document verification");
                break;
            default:

                response.setStatus("CONFIRMED");
                break;
        }
    }

    // Helper method to set default values
    private void setDefaultValues(BookingRequest entity, BookingRequestDto request) {
        if (entity.getVehicleId() == 0) {
            entity.setVehicleId(request.getVehicleId());
        }
        if (entity.getCharges() == 0) {
            entity.setCharges(request.getFinalAmount() > 0 ? request.getFinalAmount() : 1050.0f);
            log.debug("🔧 Set charges: {}", entity.getCharges());
        }
        if (entity.getFinalAmount() == 0) {
            entity.setFinalAmount(request.getFinalAmount() > 0 ? request.getFinalAmount() : 1050.0f);
            log.debug("🔧 Set final amount: {}", entity.getFinalAmount());
        }
        if (entity.getTotalHours() == 0) {
            entity.setTotalHours(calculateHoursBetweenDates(entity.getStartDate(), entity.getEndDate()));
            log.debug("🔧 Calculated total hours: {}", entity.getTotalHours());
        }
    }


    private void validateBookingRequest(BookingRequestDto request) {
        log.debug("🔍 Validating booking request...");

        if (request.getStartDate() == null) {
            log.error("❌ Validation failed: Start date is required");
            throw new IllegalArgumentException("Start date is required");
        }
        if (request.getEndDate() == null) {
            log.error("❌ Validation failed: End date is required");
            throw new IllegalArgumentException("End date is required");
        }
        if (request.getFinalAmount() <= 0) {
            log.error("❌ Validation failed: Final amount must be greater than 0, received: {}", request.getFinalAmount());
            throw new IllegalArgumentException("Final amount must be greater than 0");
        }

        log.debug("✅ Validation passed");
    }

    private float calculateHoursBetweenDates(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            log.debug("⏰ Dates are null, returning default hours: 33.0");
            return 33.0f; // Default based on your DB
        }

        long diffInMillies = endDate.getTime() - startDate.getTime();
        float hours = (float) (diffInMillies / (1000.0 * 60.0 * 60.0));
        log.debug("⏰ Calculated hours between {} and {}: {}", startDate, endDate, hours);
        return hours;
    }


    @Override
    public List<BookingBikeResponse> getAllBookingBikes() {
        return bookingRequestRepository.findAll().stream()
                .map(booking -> {
                    Bike bike = bikeRepository.findById(booking.getVehicleId()).orElse(null);
                    return mapper.toResponse(booking, bike);
                })

                .collect(Collectors.toList());
    }

    @Override
    public BookingBikeResponse getBookingBikeById(int id) {
        return bookingRequestRepository.findById(id)
                .map(booking -> {
                    Bike bike = bikeRepository.findById(booking.getVehicleId()).orElse(null);
                    return mapper.toResponse(booking, bike);
                })

                .orElseThrow(() -> new ResourceNotFoundException("BookingBike not found"));
    }

    @Override
    @Transactional
    public BookingBikeResponse acceptBooking(int bookingId) {
        try {
            log.info("✅ ACCEPT_BOOKING - Accepting booking ID: {}", bookingId);

            // ✅ Query from booking_requests table (not booking_bike)
            BookingRequest booking = bookingRequestRepository.findById(bookingId)
                    .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + bookingId));

            log.info("✅ ACCEPT_BOOKING - Found booking: {}", booking.getBookingId());

            // Update booking status to 2 (Booking Accepted)
            booking.setBookingStatus(2);
            booking.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

            // Save to database
            BookingRequest updatedBooking = bookingRequestRepository.save(booking);

            log.info("✅ ACCEPT_BOOKING - Booking {} accepted successfully", bookingId);

            Bike bike = bikeRepository.findById(updatedBooking.getVehicleId()).orElse(null);

            // Map to response
            return mapper.toResponse(updatedBooking , bike);

        } catch (ResourceNotFoundException e) {
            log.error("❌ ACCEPT_BOOKING - Booking not found: {}", bookingId);
            throw e;
        } catch (Exception e) {
            log.error("💥 ACCEPT_BOOKING - Error accepting booking {}: {}", bookingId, e.getMessage(), e);
            throw new RuntimeException("Failed to accept booking: " + e.getMessage());
        }
    }

    private String getBookingStatusName(int statusId) {
        try {
            return bookingStatusRepository.findNameById(statusId)
                    .orElse("Unknown Status (ID: " + statusId + ")");
        } catch (Exception e) {
            log.warn("⚠ Error fetching booking status name for ID {}: {}", statusId, e.getMessage());
            return "Unknown Status (ID: " + statusId + ")";
        }
    }

    @Override
    public BookingBikeResponse startTrip(String bookingId, MultipartFile[] images, Double startTripKm, HttpServletRequest httpRequest) {
        try {
            log.info("🚗 Processing trip start for booking: {}", bookingId);

            // Extract and validate customer user from JWT token
            Long userId = extractCustomerIdFromToken(httpRequest);

            // Find the booking
            BookingRequest br = find(bookingId);

            // *FIXED* - Use != for primitive int comparison
            if (br.getCustomerId() != userId.intValue()) {
                log.warn("🚫 Customer {} attempting to start trip for booking {} owned by customer {}",
                        userId, bookingId, br.getCustomerId());
                throw new UnauthorizedException("You can only start your own bookings");
            }

            // Check if booking status is ONLY "Accepted" (status = 2)
            if (br.getBookingStatus() != 2) {
                log.warn("⚠ Invalid booking state for trip start - Booking: {}, Status: {}, Customer: {}",
                        bookingId, br.getBookingStatus(), userId);
                throw new IllegalStateException("Trip can only be started from 'Accepted' status. Current status: " +
                        getBookingStatusName(br.getBookingStatus()) + ". Please wait for admin to accept your booking.");
            }

            // Validate exactly 4 images are provided
            if (images == null || images.length != 4) {
                log.warn("⚠ Invalid image count for trip start - Booking: {}, Expected: 4, Received: {}",
                        bookingId, images != null ? images.length : 0);
                throw new IllegalArgumentException("Exactly 4 bike images are required to start the trip");
            }

            // Validate each image
            for (int i = 0; i < images.length; i++) {
                if (images[i] == null || images[i].isEmpty()) {
                    throw new IllegalArgumentException("Image " + (i + 1) + " is empty or invalid");
                }
            }

            // Store trip start images in start_trip folder
            storeTripStartImages(br, images, userId.intValue());

            // Update booking status to "Start Trip" (status = 3)
            br.setStartTripKm(startTripKm);
            br.setBookingStatus(3); // Start Trip
            br.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

            BookingRequest savedBooking = bookingRequestRepository.save(br);

            log.info("✅ Trip started successfully - Booking: {}, Customer: {}, Images stored: {}, Status: {} -> {}",
                    bookingId, userId, images.length, "Accepted", "Start Trip");

            BookingBikeResponse resp = bookingRequestMapper.toBookingResponseDto(savedBooking);
            resp.setStatus("Trip Started");
            //resp.setMessage("Trip started successfully with " + images.length + " bike images uploaded");

            return resp;

        } catch (UnauthorizedException e) {
            log.error("🔐 Unauthorized trip start attempt for booking {}: {}", bookingId, e.getMessage());
            throw e;
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.warn("📋 Invalid request/state for trip start {}: {}", bookingId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("💥 Error starting trip for booking {}: {}", bookingId, e.getMessage(), e);
            throw new RuntimeException("Failed to start trip: " + e.getMessage(), e);
        }
    }

    @Override
    public BookingBikeResponse endTrip(String bookingId, MultipartFile[] images, Double endTripKm , HttpServletRequest httpRequest) {
        try {
            log.info("🏁 Processing trip end for booking: {}", bookingId);

            // Extract and validate customer user from JWT token
            Long userId = extractCustomerIdFromToken(httpRequest);

            // Find the booking
            BookingRequest br = find(bookingId);

            // *FIXED* - Use != for primitive int comparison
            if (br.getCustomerId() != userId.intValue()) {
                log.warn("🚫 Customer {} attempting to end trip for booking {} owned by customer {}",
                        userId, bookingId, br.getCustomerId());
                throw new UnauthorizedException("You can only end your own bookings");
            }

            // Check if booking status is "Start Trip" (status = 3)
            if (br.getBookingStatus() != 3) {
                log.warn("⚠ Invalid booking state for trip end - Booking: {}, Status: {}, Customer: {}",
                        bookingId, br.getBookingStatus(), userId);
                throw new IllegalStateException("Trip can only be ended from 'Start Trip' status. Current status: " +
                        getBookingStatusName(br.getBookingStatus()));
            }

            // Validate exactly 4 images are provided
            if (images == null || images.length != 4) {
                log.warn("⚠ Invalid image count for trip end - Booking: {}, Expected: 4, Received: {}",
                        bookingId, images != null ? images.length : 0);
                throw new IllegalArgumentException("Exactly 4 bike images are required to end the trip");
            }

            // Validate each image
            for (int i = 0; i < images.length; i++) {
                if (images[i] == null || images[i].isEmpty()) {
                    throw new IllegalArgumentException("Image " + (i + 1) + " is empty or invalid");
                }
            }

            // Store trip end images in end_trip folder
            storeTripEndImages(br, images, userId.intValue());

            // Update booking status to "End Trip" (status = 4)
            br.setEndTripKm(endTripKm);
            br.setBookingStatus(4); // End Trip
            br.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

            BookingRequest savedBooking = bookingRequestRepository.save(br);

            log.info("✅ Trip ended successfully - Booking: {}, Customer: {}, Images stored: {}, Status: {} -> {}",
                    bookingId, userId, images.length, "Start Trip", "End Trip");

            BookingBikeResponse resp = bookingRequestMapper.toBookingResponseDto(savedBooking);
            resp.setStatus("Trip Ended");
            //resp.setMessage("Trip ended successfully with " + images.length + " bike images uploaded");

            return resp;

        } catch (UnauthorizedException e) {
            log.error("🔐 Unauthorized trip end attempt for booking {}: {}", bookingId, e.getMessage());
            throw e;
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.warn("📋 Invalid request/state for trip end {}: {}", bookingId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("💥 Error ending trip for booking {}: {}", bookingId, e.getMessage(), e);
            throw new RuntimeException("Failed to end trip: " + e.getMessage(), e);
        }
    }


    @Override
    @Transactional
    public InvoiceDto completeBooking(int bookingId) {
        try {
            log.info("🏁 COMPLETE_BOOKING - Completing booking ID: {}", bookingId);

            // Fetch booking from booking_requests table
            BookingRequest booking = bookingRequestRepository.findById(bookingId)
                    .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + bookingId));

            // Update status to 5 (Completed)
            booking.setBookingStatus(5);
            booking.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

            BookingRequest completedBooking = bookingRequestRepository.save(booking);

            log.info("✅ COMPLETE_BOOKING - Booking {} completed successfully", bookingId);

            // ✅ Generate and return invoice
            return generateInvoice(completedBooking);

        } catch (ResourceNotFoundException e) {
            log.error("❌ COMPLETE_BOOKING - Booking not found: {}", bookingId);
            throw e;
        } catch (Exception e) {
            log.error("💥 COMPLETE_BOOKING - Error: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to complete booking: " + e.getMessage());
        }
    }


    /**
     * ✅ Generate invoice from completed booking
     */
    private InvoiceDto generateInvoice(BookingRequest booking) {
        log.info("📄 Generating invoice for booking: {}", booking.getBookingId());

        InvoiceDto invoice = new InvoiceDto();

        // Generate invoice number (format: INV-VEGO001-timestamp)
        String invoiceNumber = "INV-" + booking.getBookingId() + "-" + System.currentTimeMillis();
        invoice.setInvoiceNumber(invoiceNumber);

        // Set booking and customer details
        invoice.setBookingId(booking.getId());
        invoice.setCustomerId(booking.getCustomerId());

        // Calculate amounts
        BigDecimal amount = BigDecimal.valueOf(booking.getFinalAmount());
        BigDecimal gst = BigDecimal.valueOf(booking.getGst());

        // Calculate total (finalAmount already includes GST in your case)
        // If your finalAmount doesn't include GST, uncomment the line below
        // BigDecimal totalAmount = amount.add(gst);
        BigDecimal totalAmount = amount; // Assuming finalAmount already includes GST

        invoice.setAmount(amount);
        invoice.setTaxAmount(gst);
        invoice.setTotalAmount(totalAmount);

        // Set status and timestamps
        invoice.setStatus("PAID"); // or "PENDING" based on paymentStatus
        invoice.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        invoice.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        log.info("✅ Invoice generated: {}", invoiceNumber);

        return invoice;
    }

    @Override
    @Transactional
    public BookingBikeResponse cancelBooking(int bookingId) {
        try {
            log.info("❌ CANCEL_BOOKING - Cancelling booking ID: {}", bookingId);

            BookingRequest booking = bookingRequestRepository.findById(bookingId)
                    .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + bookingId));

            // Update status to 7 (Cancelled)
            booking.setBookingStatus(7);
            booking.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

            BookingRequest updatedBooking = bookingRequestRepository.save(booking);

            log.info("✅ CANCEL_BOOKING - Booking {} cancelled successfully", bookingId);

            Bike bike = bikeRepository.findById(updatedBooking.getVehicleId()).orElse(null);


            return mapper.toResponse(updatedBooking , bike);

        } catch (Exception e) {
            log.error("💥 CANCEL_BOOKING - Error: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to cancel booking: " + e.getMessage());
        }
    }

    @Override
    public void addAdditionalCharges(String bookingId, Float charges, String details) {
        BookingRequest booking = find(bookingId);
        booking.setAdditionalCharges(charges);
        booking.setAdditionalChargesDetails(details);
        bookingRequestRepository.save(booking);
    }

    // Helper methods
    private BookingRequest find(String bookingId) {
        return bookingRequestRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
    }

    private void storeTripImageIfPresent(BookingRequest br, MultipartFile image, int type) {
        if (image != null && !image.isEmpty()) {
            try {
                String path = imageUtils.storeImage(image,
                        type == 1 ? "booking_bikes/start" : "booking_bikes/end");
                BookingBike bb = new BookingBike();
                bb.setBookingId(br.getId());
                bb.setType(type);
                bb.setImages(path);
                bb.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                repository.save(bb);
            } catch (Exception e) {
                throw new RuntimeException("Failed to store trip image", e);
            }
        }
    }


    @Override
    public List<BookingBikeResponse> getBookingsByCustomerWithOptions(int customerId, int page, int size, String sortBy) {
        log.info("🔍 Fetching bookings for customer_id: {} with options - page: {}, size: {}, sortBy: {}",
                customerId, page, size, sortBy);

        try {
            Sort sort;
            switch (sortBy.toLowerCase()) {
                case "oldest":
                    sort = Sort.by(Sort.Direction.ASC, "createdAt");
                    break;
                case "amount":
                    sort = Sort.by(Sort.Direction.DESC, "finalAmount");
                    break;
                case "status":
                    sort = Sort.by(Sort.Direction.ASC, "bookingStatus");
                    break;
                case "latest":
                default:
                    sort = Sort.by(Sort.Direction.DESC, "createdAt");
                    break;
            }

            Pageable pageable = PageRequest.of(page, size, sort);
            Page<BookingRequest> bookingPage = bookingRequestRepository.findByCustomerId(customerId, pageable);

            log.info("✅ Found {} bookings out of {} total for customer_id: {}",
                    bookingPage.getNumberOfElements(), bookingPage.getTotalElements(), customerId);

            return bookingPage.getContent().stream()
                    .map(booking -> {
                        Bike bike = bikeRepository.findById(booking.getVehicleId()).orElse(null);
                        return mapper.toResponse(booking, bike);
                    })
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("💥 Error fetching bookings with options for customer_id {}: {}", customerId, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch bookings with options for customer_id: " + customerId, e);
        }
    }


    private void storeTripStartImages(BookingRequest booking, MultipartFile[] images, int customerId) {
        try {
            log.debug("📸 Storing {} trip start images for booking {} in start_trip folder",
                    images.length, booking.getBookingId());

            for (int i = 0; i < images.length; i++) {
                MultipartFile image = images[i];

                if (image != null && !image.isEmpty()) {
                    // Store image using ImageUtils in start_trip folder
                    String imagePath = imageUtils.storeTripStartImage(image, booking.getBookingId(), (i + 1));

                    // Create BookingBike record
                    BookingBike bb = new BookingBike();
                    bb.setBookingId(booking.getId());
                    bb.setType(1); // Type 1 = Start Trip
                    bb.setImages(imagePath);
                    bb.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                    bb.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

                    // Save to database
                    repository.save(bb);

                    log.debug("📸 Stored start trip image {} - Path: {}, BookingBike ID: {}",
                            (i + 1), imagePath, bb.getId());
                }
            }

            log.info("✅ Successfully stored {} trip start images for booking: {} (Customer: {})",
                    images.length, booking.getBookingId(), customerId);

        } catch (Exception e) {
            log.error("💥 Failed to store trip start images for booking {}: {}", booking.getBookingId(), e.getMessage());
            throw new RuntimeException("Failed to store trip start images: " + e.getMessage(), e);
        }
    }

    /**
     * Store multiple trip end images (type=2) in end_trip folder
     */
    private void storeTripEndImages(BookingRequest booking, MultipartFile[] images, int customerId) {
        try {
            log.debug("📸 Storing {} trip end images for booking {} in end_trip folder",
                    images.length, booking.getBookingId());

            for (int i = 0; i < images.length; i++) {
                MultipartFile image = images[i];

                if (image != null && !image.isEmpty()) {
                    // Store image using ImageUtils in end_trip folder
                    String imagePath = imageUtils.storeTripEndImage(image, booking.getBookingId(), (i + 1));

                    // Create BookingBike record
                    BookingBike bb = new BookingBike();
                    bb.setBookingId(booking.getId());
                    bb.setType(2); // Type 2 = End Trip
                    bb.setImages(imagePath);
                    bb.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                    bb.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

                    // Save to database
                    repository.save(bb);

                    log.debug("📸 Stored end trip image {} - Path: {}, BookingBike ID: {}",
                            (i + 1), imagePath, bb.getId());
                }
            }

            log.info("✅ Successfully stored {} trip end images for booking: {} (Customer: {})",
                    images.length, booking.getBookingId(), customerId);

        } catch (Exception e) {
            log.error("💥 Failed to store trip end images for booking {}: {}", booking.getBookingId(), e.getMessage());
            throw new RuntimeException("Failed to store trip end images: " + e.getMessage(), e);
        }
    }


    @Override
    public void updateOnlinePayment(String razorpayOrderId, String paymentId, String signature) {
        boolean verified = razorpayService.verifySignature(razorpayOrderId, paymentId, signature);
        BookingRequest booking = bookingRequestRepository.findByMerchantTransactionId(razorpayOrderId)
                .orElseThrow(() -> new RuntimeException("Booking not found for Razorpay order: " + razorpayOrderId));

        if(verified) {
            booking.setTransactionId(paymentId);
            booking.setPaymentStatus("PAID");
        } else {
            booking.setPaymentStatus("FAILED_VERIFICATION");
        }
        bookingRequestRepository.save(booking);
    }

    @Override
    public BookingBikeResponse createBookingByAdmin(BookingRequestDto bookingRequestDto) {
        // Validate bookingRequestDto as needed

        // Set booking meta for admin booking (could log admin as 'createdBy' etc)
        BookingRequest entity = mapper.toBookingRequestEntity(bookingRequestDto);
        entity.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        entity.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        entity.setBookingStatus(1);

        // Save booking
        BookingRequest savedBooking = bookingRequestRepository.save(entity);

        // Generate Booking ID if necessary
        if (savedBooking.getBookingId() == null || savedBooking.getBookingId().isEmpty()) {
            String generatedBookingId = String.format("VEGO%03d", savedBooking.getId());
            savedBooking.setBookingId(generatedBookingId);
            savedBooking = bookingRequestRepository.save(savedBooking);
        }

        // Get full bike info for response if required
        Bike bike = bikeRepository.findById(savedBooking.getVehicleId()).orElse(null);

        // Build and return response
        BookingBikeResponse response = mapper.toResponse(savedBooking, bike);
        response.setMessage("Booking created by admin successfully.");
        return response;
    }




}