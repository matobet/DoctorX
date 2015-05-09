package com.github.matobet.doctor.exception;

public class NotFoundException extends ApplicationException {

    public NotFoundException() {
        this("Not Found");
    }

    public NotFoundException(String message) {
        super(404, message);
    }
}
