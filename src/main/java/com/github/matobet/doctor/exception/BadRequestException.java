package com.github.matobet.doctor.exception;

public class BadRequestException extends ApplicationException {
    public BadRequestException(String message) {
        super(400, message);
    }
}
