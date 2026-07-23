package com.deskflow.desk.dto;

public record DeskResponse(
    Long id,
    String code,
    Integer floor,
    Boolean hasMonitor,
    Boolean active
) {
}
