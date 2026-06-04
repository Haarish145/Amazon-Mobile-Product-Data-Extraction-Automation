package com.amazon.automation.exceptions;

public class CaptchaDetectedException extends RuntimeException {
    public CaptchaDetectedException(String message) {
        super(message);
    }
}
