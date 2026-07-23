package com.deskflow.desk;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "desk")
public class Desk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private Integer floor;

    @Column(name = "has_monitor", nullable = false)
    private Boolean hasMonitor;

    @Column(name = "is_active", nullable = false)
    private Boolean active;

    protected Desk() {
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public Integer getFloor() {
        return floor;
    }

    public Boolean getHasMonitor() {
        return hasMonitor;
    }

    public Boolean getActive() {
        return active;
    }
}
