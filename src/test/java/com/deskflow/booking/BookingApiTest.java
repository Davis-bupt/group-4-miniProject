package com.deskflow.booking;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.deskflow.desk.DeskRepository;

/**
 * Endpoint 3 and 4 integration coverage for the booking API.
 */
@SpringBootTest
@AutoConfigureMockMvc
class BookingApiTest {

    private static final Long ACTIVE_DESK_ID = 2L;
    private static final Long INACTIVE_DESK_ID = 3L;
    private static final Long UNKNOWN_DESK_ID = 99999L;
    private static final Long CONFLICT_DESK_ID = 1L;

    private static final LocalDate CREATED_DATE = LocalDate.of(2099, 1, 1);
    private static final LocalDate LIST_DATE = LocalDate.of(2099, 1, 2);
    private static final LocalDate CONFLICT_DATE = LocalDate.of(2026, 7, 24);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private DeskRepository deskRepository;

    @Test
    void createBookingReturns201AndBookingResponse() throws Exception {
        String requestBody = """
                {
                  "deskId": 2,
                  "employeeName": "Anna Kowalska",
                  "date": "2099-01-01"
                }
                """;

        mockMvc.perform(post("/api/bookings")
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.deskId").value(ACTIVE_DESK_ID.intValue()))
                .andExpect(jsonPath("$.deskCode").value(deskRepository.findById(ACTIVE_DESK_ID).orElseThrow().getCode()))
                .andExpect(jsonPath("$.employeeName").value("Anna Kowalska"))
                .andExpect(jsonPath("$.date").value(CREATED_DATE.toString()))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void createBookingWithUnknownDeskReturns404() throws Exception {
        String requestBody = """
                {
                  "deskId": 99999,
                  "employeeName": "Anna Kowalska",
                  "date": "2099-01-03"
                }
                """;

        mockMvc.perform(post("/api/bookings")
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Desk 99999 not found"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void createBookingWithInactiveDeskReturns400() throws Exception {
        String requestBody = """
                {
                  "deskId": 3,
                  "employeeName": "Anna Kowalska",
                  "date": "2099-01-04"
                }
                """;

        mockMvc.perform(post("/api/bookings")
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Desk 3 is inactive"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void createBookingWithBlankEmployeeNameReturns400WithDocumentedMessage() throws Exception {
        String requestBody = """
                {
                  "deskId": 2,
                  "employeeName": "   ",
                  "date": "2099-01-05"
                }
                """;

        mockMvc.perform(post("/api/bookings")
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("employeeName must not be blank"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void createBookingWithMissingDateReturns400WithDocumentedMessage() throws Exception {
        String requestBody = """
                {
                  "deskId": 2,
                  "employeeName": "Anna Kowalska"
                }
                """;

        mockMvc.perform(post("/api/bookings")
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("date is required"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void createBookingWithInvalidDateReturns400WithDocumentedMessage() throws Exception {
        String requestBody = """
                {
                  "deskId": 2,
                  "employeeName": "Anna Kowalska",
                  "date": "not-a-date"
                }
                """;

        mockMvc.perform(post("/api/bookings")
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("date is required"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void createBookingThatConflictsReturns409() throws Exception {
        String requestBody = """
                {
                  "deskId": 1,
                  "employeeName": "Anna Kowalska",
                  "date": "2026-07-24"
                }
                """;

        mockMvc.perform(post("/api/bookings")
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("CONFLICT"))
                .andExpect(jsonPath("$.message").value("Desk already booked for this date"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void listBookingsForDateReturns200AndMatchingBookings() throws Exception {
        bookingRepository.save(new Booking(ACTIVE_DESK_ID, "List Test", LIST_DATE));

        mockMvc.perform(get("/api/bookings")
                        .param("date", LIST_DATE.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].deskId").value(ACTIVE_DESK_ID.intValue()))
                .andExpect(jsonPath("$[0].employeeName").value("List Test"))
                .andExpect(jsonPath("$[0].date").value(LIST_DATE.toString()))
                .andExpect(jsonPath("$[0].deskCode").value(deskRepository.findById(ACTIVE_DESK_ID).orElseThrow().getCode()))
                .andExpect(jsonPath("$[0].createdAt").exists());
    }

    @Test
    void listBookingsWithoutDateReturns400WithDocumentedMessage() throws Exception {
        mockMvc.perform(get("/api/bookings"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("date is required"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void listBookingsWithInvalidDateReturns400WithDocumentedMessage() throws Exception {
        mockMvc.perform(get("/api/bookings")
                        .param("date", "not-a-date"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("date is required"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}