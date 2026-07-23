package com.deskflow.booking;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Persistence for {@link Booking}. JpaRepository gives us save / findById /
 * deleteById; the derived queries below back the list-by-date, conflict check,
 * and available-desk features.
 */
public interface BookingRepository extends JpaRepository<Booking, Long> {

    /** Endpoint 4: bookings on a given date. */
    List<Booking> findByDate(LocalDate date);

    /** Service-layer double-booking check (endpoint 3, before insert). */
    boolean existsByDeskIdAndDate(Long deskId, LocalDate date);

    /** Creative endpoint C: desk ids already taken on a date, to exclude them. */
    List<Booking> findByDateAndDeskIdIn(LocalDate date, List<Long> deskIds);
}
