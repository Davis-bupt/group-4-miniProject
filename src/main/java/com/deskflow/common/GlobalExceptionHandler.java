package com.deskflow.common;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Single place that maps exceptions to the unified {@link ApiError} body
 * (ARCHITECTURE.md §8). Controllers stay thin — they throw, this translates.
 * Shared by the whole team; every endpoint's 400/404/409 flows through here.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(NotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, "NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleBadRequest(BadRequestException ex) {
        return build(HttpStatus.BAD_REQUEST, "BAD_REQUEST", ex.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiError> handleConflict(ConflictException ex) {
        return build(HttpStatus.CONFLICT, "CONFLICT", ex.getMessage());
    }

    /** @Valid failures on request bodies (e.g. blank employeeName, null deskId). */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldError();
        String message = fieldError != null
                ? fieldError.getField() + " " + fieldError.getDefaultMessage()
                : "Validation failed";
        return build(HttpStatus.BAD_REQUEST, "BAD_REQUEST", message);
    }

    /** Missing required query param, e.g. GET /api/bookings without date. */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiError> handleMissingParam(MissingServletRequestParameterException ex) {
        return build(HttpStatus.BAD_REQUEST, "BAD_REQUEST", ex.getParameterName() + " is required");
    }

    /**
     * Wrong-typed query param (e.g. date=not-a-date, floor=abc) or an
     * unparseable request body (e.g. a malformed "date" field in POST
     * /api/bookings).
     *
     * <p>API.md #3/#4 documents one message — "date is required" — for both
     * the missing-date case (handled above by handleMissingParam) and the
     * invalid-date-format case handled here, so the {@code date}
     * param/field is special-cased to literally match that wording. Any
     * other mistyped param/field gets a generic message since the doc
     * doesn't specify wording for those.
     */
    @ExceptionHandler({MethodArgumentTypeMismatchException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<ApiError> handleTypeMismatch(Exception ex) {
        if (isDateRelated(ex)) {
            return build(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "date is required");
        }
        return build(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "Invalid or malformed request parameter");
    }

    private boolean isDateRelated(Exception ex) {
        if (ex instanceof MethodArgumentTypeMismatchException mismatch) {
            return "date".equals(mismatch.getName());
        }
        if (ex.getCause() instanceof InvalidFormatException invalidFormat) {
            return invalidFormat.getPath().stream()
                    .anyMatch(ref -> "date".equals(ref.getFieldName()));
        }
        return false;
    }

    /** Fallback — anything unmapped becomes a 500 rather than leaking a stack trace. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception ex) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "Unexpected error");
    }

    private ResponseEntity<ApiError> build(HttpStatus status, String error, String message) {
        return ResponseEntity.status(status).body(ApiError.of(error, message));
    }
}
