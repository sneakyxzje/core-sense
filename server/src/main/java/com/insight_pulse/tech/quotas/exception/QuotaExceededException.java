package com.insight_pulse.tech.quotas.exception;

public class QuotaExceededException extends RuntimeException{
    public QuotaExceededException(String message) {
        super(message);
    }
}
