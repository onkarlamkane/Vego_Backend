package com.eptiq.vegobike.mappers;

import com.eptiq.vegobike.dtos.BikeResponseDTO;
import com.eptiq.vegobike.dtos.BookingBikeResponse;
import com.eptiq.vegobike.dtos.BookingRequestDto;
import com.eptiq.vegobike.model.Bike;
import com.eptiq.vegobike.model.BookingRequest;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BookingBikeMapper {

    // Map BookingRequest and Bike to BookingBikeResponse including nested bikeDetails
    @Mapping(source = "booking.id", target = "id")
    @Mapping(source = "booking.bookingId", target = "bookingId")
    @Mapping(source = "booking.vehicleId", target = "vehicleId")
    @Mapping(source = "booking.customerId", target = "customerId")
    @Mapping(source = "booking.startDate", target = "startDate")
    @Mapping(source = "booking.endDate", target = "endDate")
    @Mapping(source = "booking.startDate1", target = "startDate1")
    @Mapping(source = "booking.endDate1", target = "endDate1")
    @Mapping(source = "booking.charges", target = "charges")
    @Mapping(source = "booking.additionalCharges", target = "additionalCharges")
    @Mapping(source = "booking.additionalChargesDetails", target = "additionalChargesDetails")
    @Mapping(source = "booking.additionalHours", target = "additionalHours")
    @Mapping(source = "booking.advanceAmount", target = "advanceAmount")
    @Mapping(source = "booking.totalHours", target = "totalHours")
    @Mapping(source = "booking.gst", target = "gst")
    @Mapping(source = "booking.km", target = "km")
    @Mapping(source = "booking.deliveryCharges", target = "deliveryCharges")
    @Mapping(source = "booking.totalCharges", target = "totalCharges")
    @Mapping(source = "booking.finalAmount", target = "finalAmount")
    @Mapping(source = "booking.pickupLocationId", target = "pickupLocationId")
    @Mapping(source = "booking.dropLocationId", target = "dropLocationId")
    @Mapping(source = "booking.bookingStatus", target = "bookingStatus")
    @Mapping(source = "booking.bookingStatus", target = "status", qualifiedByName = "mapBookingStatusToDisplay")
    @Mapping(source = "booking.paymentType", target = "paymentType")
    @Mapping(source = "booking.addressType", target = "addressType")
    @Mapping(source = "booking.address", target = "address")
    @Mapping(source = "booking.transactionId", target = "transactionId")
    @Mapping(source = "booking.createdAt", target = "createdAt")
    @Mapping(source = "booking.updatedAt", target = "updatedAt")
    @Mapping(source = "booking.couponId", target = "couponId")
    @Mapping(source = "booking.couponCode", target = "couponCode")
    @Mapping(source = "booking.couponAmount", target = "couponAmount")
    @Mapping(source = "booking.deliveryType", target = "deliveryType")
    @Mapping(source = "booking.lateFeeCharges", target = "lateFeeCharges")
    @Mapping(source = "booking.lateEndDate", target = "lateEndDate")
    @Mapping(source = "booking.merchantTransactionId", target = "merchantTransactionId")
    @Mapping(source = "booking.paymentStatus", target = "paymentStatus")
    @Mapping(source = "bike", target = "bikeDetails")
    BookingBikeResponse toResponse(BookingRequest booking, Bike bike);

    // Map Bike entity to BikeResponseDTO with int-to-boolean conversion for flags
    @Mapping(target = "isPuc", source = "isPuc", qualifiedByName = "intToBoolean")
    @Mapping(target = "isInsurance", source = "isInsurance", qualifiedByName = "intToBoolean")
    @Mapping(target = "isDocuments", source = "isDocuments", qualifiedByName = "intToBoolean")
    @Mapping(target = "isActive", source = "isActive", qualifiedByName = "intToBoolean")
    BikeResponseDTO toBikeResponseDTO(Bike bike);

    // DTO to entity conversion for BookingRequest
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bookingId", ignore = true)
    @Mapping(target = "customerId", ignore = true)
    @Mapping(target = "bookingStatus", ignore = true)
    @Mapping(target = "paymentStatus", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "merchantTransactionId", ignore = true)
    @Mapping(target = "transactionId", ignore = true)
    BookingRequest toBookingRequestEntity(BookingRequestDto dto);
    @Named("intToBoolean")
    default boolean intToBoolean(int value) {
        return value != 0;
    }

    @Named("mapBookingStatusToDisplay")
    default String mapBookingStatusToDisplay(int bookingStatus) {
        switch (bookingStatus) {
            case 1: return "Confirmed";
            case 2: return "Accepted";
            case 3: return "Start Trip";
            case 4: return "End Trip";
            case 5: return "Completed";           // Updated id for Completed
            case 6: return "Document Upload";     // Updated id for Document Upload
            case 7: return "Cancelled";           // Updated id for Cancelled
            default: return "Unknown";
        }
    }

    @Named("convertToDateOnly")
    default java.util.Date convertToDateOnly(java.util.Date dateTime) {
        if (dateTime == null) return null;
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(dateTime);
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
        cal.set(java.util.Calendar.MINUTE, 0);
        cal.set(java.util.Calendar.SECOND, 0);
        cal.set(java.util.Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
}