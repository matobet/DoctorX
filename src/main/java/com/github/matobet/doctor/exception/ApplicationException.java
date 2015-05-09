package com.github.matobet.doctor.exception;

public class ApplicationException extends RuntimeException {

    private final int statusCode;

    protected ApplicationException(int statusCode) {
        this.statusCode = statusCode;
    }

    protected ApplicationException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
