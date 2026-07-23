package com.deskflow.desk;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.deskflow.booking.Booking;
import com.deskflow.booking.BookingRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Creative endpoint C — GET /api/desks/available.
 * Runs against the H2 dev profile; each test builds its own desks/bookings via
 * the repositories, so the DB is cleared first to stay independent of other tests.
 */
@SpringBootTest
@AutoConfigureMockMvc
class DeskAvailableTest {

    private static final LocalDate DATE = LocalDate.of(2026, 7, 24);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DeskRepository deskRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @BeforeEach
    void reset() {
        bookingRepository.deleteAll();
        deskRepository.deleteAll();
    }

    @Test
    void returnsOnlyActiveDesksNotBookedOnThatDate() throws Exception {
        Desk free = deskRepository.save(new Desk("KRK-3F-11", 3, true, true));
        Desk booked = deskRepository.save(new Desk("KRK-3F-12", 3, false, true));
        deskRepository.save(new Desk("KRK-3F-13", 3, false, false)); // inactive -> excluded

        bookingRepository.save(new Booking(booked.getId(), "Anna Kowalska", DATE));

        mockMvc.perform(get("/api/desks/available").param("date", "2026-07-24"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(free.getId()))
                .andExpect(jsonPath("$[0].code").value("KRK-3F-11"));
    }

    @Test
    void floorParamNarrowsResults() throws Exception {
        deskRepository.save(new Desk("KRK-2F-01", 2, false, true));
        Desk thirdFloor = deskRepository.save(new Desk("KRK-3F-01", 3, false, true));

        mockMvc.perform(get("/api/desks/available")
                        .param("date", "2026-07-24")
                        .param("floor", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(thirdFloor.getId()));
    }

    @Test
    void noFreeDeskReturnsEmptyArray() throws Exception {
        Desk only = deskRepository.save(new Desk("KRK-2F-02", 2, false, true));
        bookingRepository.save(new Booking(only.getId(), "Jan Nowak", DATE));

        mockMvc.perform(get("/api/desks/available").param("date", "2026-07-24"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void missingDateReturns400WithUnifiedErrorBody() throws Exception {
        mockMvc.perform(get("/api/desks/available"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void malformedDateReturns400() throws Exception {
        mockMvc.perform(get("/api/desks/available").param("date", "24-07-2026"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }
}
