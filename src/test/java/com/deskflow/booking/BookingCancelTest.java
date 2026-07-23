package com.deskflow.booking;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Endpoint 5 — DELETE /api/bookings/{id}.
 * Runs against the H2 dev profile (Hibernate auto-creates the tables), so no
 * seed data is required — the test inserts its own booking via the repository.
 */
@SpringBootTest
@AutoConfigureMockMvc
class BookingCancelTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookingRepository bookingRepository;

    @Test
    void deletingExistingBookingReturns204AndRemovesIt() throws Exception {
        Booking saved = bookingRepository.save(
                new Booking(1L, "Anna Kowalska", LocalDate.of(2026, 7, 24)));

        mockMvc.perform(delete("/api/bookings/{id}", saved.getId()))
                .andExpect(status().isNoContent());

        // Second delete of the same id now 404s — proves the row was removed.
        mockMvc.perform(delete("/api/bookings/{id}", saved.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletingUnknownBookingReturns404WithUnifiedErrorBody() throws Exception {
        mockMvc.perform(delete("/api/bookings/{id}", 99999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
