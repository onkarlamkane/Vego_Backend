package com.eptiq.vegobike.exceptions;

/**
 * Exception thrown when user is not found in the system
 */
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
