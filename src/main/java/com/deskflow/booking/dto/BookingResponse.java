package com.deskflow.booking.dto;

import com.deskflow.booking.Booking;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Response shape for booking endpoints (API.md #3, #4).
 *
 * <p>{@code createdAt} is {@link Instant}, matching {@code Booking.createdAt}
 * (also {@code Instant}, set via {@code @CreationTimestamp}) — Jackson
 * serializes it in the UTC "...Z" format API.md documents
 * (e.g. {@code "2026-07-23T10:15:00Z"}), no conversion needed here.
 */
public record BookingResponse(
        Long id,
        Long deskId,
        String deskCode,
        String employeeName,
        LocalDate date,
        Instant createdAt
) {
    /**
     * Booking has no JPA reference to Desk (deskId is a plain column), so the
     * desk code must be looked up separately and passed in here.
     */
    public static BookingResponse from(Booking booking, String deskCode) {
        return new BookingResponse(
                booking.getId(),
                booking.getDeskId(),
                deskCode,
                booking.getEmployeeName(),
                booking.getDate(),
                booking.getCreatedAt()
        );
    }
}
