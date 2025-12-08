package dev.sethan8r.mama.exception;

import java.util.List;

public record ValidationErrorResponse(
        String message,
        List<FieldError> errors
) {
    public record FieldError(String field, String message) {}
}
