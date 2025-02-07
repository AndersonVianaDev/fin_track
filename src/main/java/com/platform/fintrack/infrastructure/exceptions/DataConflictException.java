package com.platform.fintrack.infrastructure.exceptions;

public class DataConflictException extends RuntimeException {
    public DataConflictException(String message) {
        super(message);
    }
}
