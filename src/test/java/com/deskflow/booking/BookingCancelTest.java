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
 * Runs against the H2 dev profile. schema.sql/data.sql now auto-seed real
 * desks/bookings on startup (see ARCHITECTURE.md §9), so this test uses a
 * far-future date to avoid colliding with the (desk_id, date) unique
 * constraint on the seeded rows.
 */
@SpringBootTest
@AutoConfigureMockMvc
class BookingCancelTest {

    private static final LocalDate UNSEEDED_DATE = LocalDate.of(2099, 1, 1);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookingRepository bookingRepository;

    @Test
    void deletingExistingBookingReturns204AndRemovesIt() throws Exception {
        Booking saved = bookingRepository.save(
                new Booking(1L, "Anna Kowalska", UNSEEDED_DATE));

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
