package com.eptiq.vegobike.exceptions;

/**
 * Exception thrown when trying to create a new booking while the customer has an active booking
 */
public class ActiveBookingExistsException extends RuntimeException {

    public ActiveBookingExistsException(String message) {
        super(message);
    }

    public ActiveBookingExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
