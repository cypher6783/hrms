package com.hospital.resource.common.exception;

import org.springframework.http.HttpStatus;

import java.util.List;

public class ValidationException extends BusinessException {

    private final List<FieldError> fieldErrors;

    public ValidationException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "VALIDATION_ERROR");
        this.fieldErrors = List.of();
    }

    public ValidationException(List<FieldError> fieldErrors) {
        super("Validation failed", HttpStatus.BAD_REQUEST, "VALIDATION_ERROR");
        this.fieldErrors = fieldErrors;
    }

    public List<FieldError> getFieldErrors() {
        return fieldErrors;
    }

    public record FieldError(String field, String message, Object rejectedValue) {}
}
