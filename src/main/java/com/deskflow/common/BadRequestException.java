package com.deskflow.common;

/** Thrown for invalid input the annotations can't cover (inactive desk, bad date) → HTTP 400. */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
