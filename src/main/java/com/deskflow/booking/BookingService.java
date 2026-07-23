package com.deskflow.booking;

import com.deskflow.common.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Business logic for bookings. Endpoint 5 (cancel) lives here; other booking
 * operations (create/list) will be added by their owners in this same class.
 */
@Service
public class BookingService {

    private final BookingRepository bookingRepository;

    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    /**
     * Endpoint 5 — cancel a booking by id.
     *
     * @throws NotFoundException if no booking has that id (→ 404 via GlobalExceptionHandler)
     */
    @Transactional
    public void cancel(Long id) {
        if (!bookingRepository.existsById(id)) {
            throw new NotFoundException("Booking " + id + " not found");
        }
        bookingRepository.deleteById(id);
    }
}
