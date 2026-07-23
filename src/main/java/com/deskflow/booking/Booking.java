package com.deskflow.booking;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import java.time.LocalDate;
import org.hibernate.annotations.CreationTimestamp;

/**
 * Booking entity — maps to the {@code booking} table.
 *
 * <p>Core rule: a desk may have at most one booking per date. Enforced at the DB
 * layer by the unique constraint on (desk_id, date); the service layer also
 * checks before insert. See ARCHITECTURE.md §7 (two-layer enforcement).
 */
@Entity
@Table(
        name = "booking",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_booking_desk_date",
                columnNames = {"desk_id", "date"}
        )
)
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * FK to desk. Stored as a plain id column (not a JPA relationship) to keep the
     * booking module decoupled from the Desk entity — the service looks up the desk
     * via DeskRepository when it needs desk details.
     */
    @Column(name = "desk_id", nullable = false)
    private Long deskId;

    @Column(name = "employee_name", nullable = false)
    private String employeeName;

    @Column(nullable = false)
    private LocalDate date;

    /**
     * Set automatically on insert. Instant (not LocalDateTime) so it captures
     * a true UTC point in time and Jackson serializes it with a trailing "Z",
     * matching API.md's documented createdAt format exactly
     * (e.g. "2026-07-23T10:15:00Z") regardless of the server's local zone.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected Booking() {
        // JPA requires a no-arg constructor.
    }

    public Booking(Long deskId, String employeeName, LocalDate date) {
        this.deskId = deskId;
        this.employeeName = employeeName;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public Long getDeskId() {
        return deskId;
    }

    public void setDeskId(Long deskId) {
        this.deskId = deskId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
