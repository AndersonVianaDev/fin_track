package com.platform.fintrack.infrastructure.exceptions;

public class UnexpectedException extends RuntimeException{
    public UnexpectedException(String message) {
        super(message);
    }
}
