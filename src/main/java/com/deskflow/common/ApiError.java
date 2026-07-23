package com.deskflow.common;

import java.time.OffsetDateTime;

/**
 * Unified error response body (ARCHITECTURE.md §8 / API.md).
 * Shared by all endpoints — every non-2xx response has this exact shape:
 * <pre>{ "error": "CONFLICT", "message": "...", "timestamp": "2026-07-24T10:00:00Z" }</pre>
 */
public record ApiError(String error, String message, OffsetDateTime timestamp) {

    public static ApiError of(String error, String message) {
        return new ApiError(error, message, OffsetDateTime.now());
    }
}
