package com.deskflow.common;

/** Thrown when a referenced resource (desk, booking) does not exist → HTTP 404. */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
