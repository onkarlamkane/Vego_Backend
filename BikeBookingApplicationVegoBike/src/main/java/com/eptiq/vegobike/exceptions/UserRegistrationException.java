package com.eptiq.vegobike.exceptions;

/**
 * Exception thrown when user registration fails
 */
public class UserRegistrationException extends RuntimeException {

    public UserRegistrationException(String message) {
        super(message);
    }

    public UserRegistrationException(String message, Throwable cause) {
        super(message, cause);
    }
}
