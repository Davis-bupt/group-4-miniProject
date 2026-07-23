package com.deskflow.desk;

import com.deskflow.booking.Booking;
import com.deskflow.booking.BookingRepository;
import com.deskflow.desk.dto.DeskResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * Business logic for the creative endpoint C ({@code GET /api/desks/available}).
 * Keeps the availability rule out of the controller so the desk-listing endpoint
 * (endpoint 2) stays untouched.
 */
@Service
public class DeskService {

    private final DeskRepository deskRepository;
    private final BookingRepository bookingRepository;

    public DeskService(DeskRepository deskRepository, BookingRepository bookingRepository) {
        this.deskRepository = deskRepository;
        this.bookingRepository = bookingRepository;
    }

    /**
     * Desks that are active and not booked on the given date. Optional floor filter.
     * Returns an empty list when nothing is free (never null).
     */
    public List<DeskResponse> findAvailable(LocalDate date, Integer floor) {
        // Desk ids already taken on that date -> exclude them.
        Set<Long> takenDeskIds = bookingRepository.findByDate(date).stream()
                .map(Booking::getDeskId)
                .collect(Collectors.toSet());

        return deskRepository.findByActiveTrue().stream()
                .filter(desk -> floor == null || desk.getFloor() == floor)
                .filter(desk -> !takenDeskIds.contains(desk.getId()))
                .map(desk -> new DeskResponse(
                        desk.getId(),
                        desk.getCode(),
                        desk.getFloor(),
                        desk.isHasMonitor(),
                        desk.isActive()))
                .toList();
    }
}
