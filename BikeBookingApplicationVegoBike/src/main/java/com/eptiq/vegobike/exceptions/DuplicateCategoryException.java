package com.eptiq.vegobike.exceptions;

public class DuplicateCategoryException extends RuntimeException {
    public DuplicateCategoryException(String message) {
        super(message);
    }
}