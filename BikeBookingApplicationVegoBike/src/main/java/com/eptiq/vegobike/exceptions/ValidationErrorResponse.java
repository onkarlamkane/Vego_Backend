package com.eptiq.vegobike.exceptions;

import java.time.LocalDateTime;
import java.util.Map;

public record ValidationErrorResponse(
        String error,
        String message,
        Map<String, String> details,
        LocalDateTime timestamp,
        int status
) {}
