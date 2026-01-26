package com.insight_pulse.tech.exception.except;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidEmailException extends RuntimeException {
    private static final String CODE = "INVALID_EMAIL";
    public InvalidEmailException(String message) {
        super(message);
    }

    public String getCode() {
        return CODE;
    }
}
