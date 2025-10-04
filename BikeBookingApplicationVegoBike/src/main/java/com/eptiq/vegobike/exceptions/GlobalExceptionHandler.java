package com.eptiq.vegobike.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class    GlobalExceptionHandler {

    @ExceptionHandler(UserRegistrationException.class)
    public ResponseEntity<ApiErrorResponse> handleRegistrationException(UserRegistrationException ex) {
        log.error("User registration failed: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(new ApiErrorResponse(
                        "REGISTRATION_FAILED",
                        ex.getMessage(),
                        LocalDateTime.now(),
                        HttpStatus.BAD_REQUEST.value()
                ));
    }

    @ExceptionHandler(InvalidOTPException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidOTP(InvalidOTPException ex) {
        log.warn("Invalid OTP attempt: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(new ApiErrorResponse(
                        "INVALID_OTP",
                        ex.getMessage(),
                        LocalDateTime.now(),
                        HttpStatus.BAD_REQUEST.value()
                ));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        log.warn("User not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiErrorResponse(
                        "USER_NOT_FOUND",
                        ex.getMessage(),
                        LocalDateTime.now(),
                        HttpStatus.NOT_FOUND.value()
                ));
    }

    @ExceptionHandler(CustomAuthenticationException.class)
    public ResponseEntity<ApiErrorResponse> handleAuthenticationException(CustomAuthenticationException ex) {
        log.warn("Authentication failed: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiErrorResponse(
                        "AUTHENTICATION_FAILED",
                        ex.getMessage(),
                        LocalDateTime.now(),
                        HttpStatus.UNAUTHORIZED.value()
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        log.warn("Validation failed: {}", errors);

        return ResponseEntity.badRequest().body(new ValidationErrorResponse(
                "VALIDATION_FAILED",
                "Input validation failed",
                errors,
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value()
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiErrorResponse(
                        "INTERNAL_SERVER_ERROR",
                        "An unexpected error occurred. Please try again later.",
                        LocalDateTime.now(),
                        HttpStatus.INTERNAL_SERVER_ERROR.value()
                ));
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateResource(DuplicateResourceException ex) {
        log.warn("Duplicate resource: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiErrorResponse(
                        "DUPLICATE_RESOURCE",
                        ex.getMessage(),
                        LocalDateTime.now(),
                        HttpStatus.CONFLICT.value()
                ));
    }


}
