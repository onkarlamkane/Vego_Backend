package com.eptiq.vegobike.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ApiErrorResponse {
    private String errorCode;
    private String message;
    private LocalDateTime timestamp;
    private int status;
}
