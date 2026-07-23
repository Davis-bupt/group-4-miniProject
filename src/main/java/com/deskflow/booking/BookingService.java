package com.deskflow.booking;

import com.deskflow.booking.dto.BookingResponse;
import com.deskflow.booking.dto.CreateBookingRequest;
import com.deskflow.common.BadRequestException;
import com.deskflow.common.ConflictException;
import com.deskflow.common.NotFoundException;
import com.deskflow.desk.Desk;
import com.deskflow.desk.DeskRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Business logic for bookings: create (3), list-by-date (4), cancel (5). */
@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final DeskRepository deskRepository;

    public BookingService(BookingRepository bookingRepository, DeskRepository deskRepository) {
        this.bookingRepository = bookingRepository;
        this.deskRepository = deskRepository;
    }

    /**
     * Validation order (API.md #3, do not reorder): desk exists (404) -> desk
     * active (400) -> conflict check (409) -> save (201).
     * DataIntegrityViolationException is a race-condition fallback backed by
     * the DB unique constraint on (desk_id, date) (ARCHITECTURE.md §7).
     */
    @Transactional
    public BookingResponse createBooking(CreateBookingRequest request) {
        Desk desk = deskRepository.findById(request.deskId())
                .orElseThrow(() -> new NotFoundException("Desk " + request.deskId() + " not found"));

        if (!desk.isActive()) {
            throw new BadRequestException("Desk " + desk.getId() + " is inactive");
        }

        if (bookingRepository.existsByDeskIdAndDate(desk.getId(), request.date())) {
            throw new ConflictException("Desk already booked for this date");
        }

        try {
            Booking saved = bookingRepository.save(
                    new Booking(desk.getId(), request.employeeName(), request.date()));
            return BookingResponse.from(saved, desk.getCode());
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException("Desk already booked for this date");
        }
    }

    /**
     * GET /api/bookings?date= (API.md #4). Desk codes are batch-fetched via
     * findAllById to avoid an N+1 query per booking.
     */
    public List<BookingResponse> listBookingsForDate(LocalDate date) {
        List<Booking> bookings = bookingRepository.findByDate(date);

        List<Long> deskIds = bookings.stream()
                .map(Booking::getDeskId)
                .distinct()
                .toList();
        Map<Long, String> codesByDeskId = deskRepository.findAllById(deskIds).stream()
                .collect(Collectors.toMap(Desk::getId, Desk::getCode));

        return bookings.stream()
                .map(booking -> BookingResponse.from(booking, codesByDeskId.get(booking.getDeskId())))
                .toList();
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
