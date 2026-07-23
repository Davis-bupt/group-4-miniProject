package com.deskflow.desk.dto;

public record DeskResponse(
    Long id,
    String code,
    int floor,
    boolean hasMonitor,
    boolean active
) {
}
