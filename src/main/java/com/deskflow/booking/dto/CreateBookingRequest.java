package com.deskflow.booking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/** Request body for POST /api/bookings (API.md #3). */
public record CreateBookingRequest(
        @NotNull Long deskId,
        @NotBlank String employeeName,
        @NotNull LocalDate date
) {
}
