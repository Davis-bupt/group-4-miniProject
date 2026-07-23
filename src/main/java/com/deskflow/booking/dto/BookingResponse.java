package com.deskflow.booking.dto;

import com.deskflow.booking.Booking;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Response shape for booking endpoints (API.md #3, #4). */
public record BookingResponse(
        Long id,
        Long deskId,
        String deskCode,
        String employeeName,
        LocalDate date,
        LocalDateTime createdAt
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
