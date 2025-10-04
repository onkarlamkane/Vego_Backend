package com.eptiq.vegobike.exceptions;


public class DocumentVerificationException extends RuntimeException {
    public DocumentVerificationException(String message) {
        super(message);
    }

    public DocumentVerificationException(String message, Throwable cause) {
        super(message, cause);
    }
}