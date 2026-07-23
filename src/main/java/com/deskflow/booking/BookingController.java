package com.deskflow.booking;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST endpoints for bookings.
 *
 * <p>Owns endpoint 5 (cancel). Endpoints 3 (create) and 4 (list-by-date) share
 * this controller — their owners add @PostMapping / @GetMapping methods here
 * rather than creating a second BookingController.
 */
@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    /** Endpoint 5 — DELETE /api/bookings/{id}: 204 on success, 404 if unknown id. */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@PathVariable Long id) {
        bookingService.cancel(id);
    }
}
