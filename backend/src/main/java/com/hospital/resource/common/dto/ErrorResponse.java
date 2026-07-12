package com.hospital.resource.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        boolean success,
        String message,
        String code,
        List<FieldError> fieldErrors,
        Instant timestamp
) {
    public record FieldError(String field, String message, Object rejectedValue) {}

    public static ErrorResponse of(String message, String code) {
        return new ErrorResponse(false, message, code, null, Instant.now());
    }

    public static ErrorResponse of(String message, String code, List<FieldError> fieldErrors) {
        return new ErrorResponse(false, message, code, fieldErrors, Instant.now());
    }
}
