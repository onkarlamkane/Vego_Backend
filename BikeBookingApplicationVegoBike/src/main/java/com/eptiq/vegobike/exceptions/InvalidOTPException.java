package com.eptiq.vegobike.exceptions;

/**
 * Exception thrown when OTP validation fails
 */
public class InvalidOTPException extends RuntimeException {

    public InvalidOTPException(String message) {
        super(message);
    }

    public InvalidOTPException(String message, Throwable cause) {
        super(message, cause);
    }
}
