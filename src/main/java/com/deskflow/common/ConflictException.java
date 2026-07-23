package com.deskflow.common;

/** Thrown when the double-booking rule is violated (desk already booked that date) → HTTP 409. */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
