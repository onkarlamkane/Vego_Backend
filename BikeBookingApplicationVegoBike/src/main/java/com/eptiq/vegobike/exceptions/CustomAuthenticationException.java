package com.eptiq.vegobike.exceptions;

/**
 * Custom authentication exception to avoid conflicts with Spring Security's AuthenticationException
 */
public class CustomAuthenticationException extends RuntimeException {

    public CustomAuthenticationException(String message) {
        super(message);
    }

    public CustomAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
