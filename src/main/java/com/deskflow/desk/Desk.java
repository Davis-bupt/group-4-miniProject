package com.deskflow.desk;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Desk entity — maps to the {@code desk} table.
 * One row = one bookable (or inactive) hot desk.
 */
@Entity
@Table(name = "desk")
public class Desk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Unique short code, e.g. "KRK-3F-12". */
    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private int floor;

    @Column(name = "has_monitor", nullable = false)
    private boolean hasMonitor;

    /** Inactive desks cannot be booked. */
    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    protected Desk() {
        // JPA requires a no-arg constructor.
    }

    public Desk(String code, int floor, boolean hasMonitor, boolean active) {
        this.code = code;
        this.floor = floor;
        this.hasMonitor = hasMonitor;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public boolean isHasMonitor() {
        return hasMonitor;
    }

    public void setHasMonitor(boolean hasMonitor) {
        this.hasMonitor = hasMonitor;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
