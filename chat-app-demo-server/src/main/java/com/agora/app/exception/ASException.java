package com.agora.app.exception;

public class ASException extends RuntimeException {
    public ASException(String message) {
        super(message);
    }

    public ASException(String message, Throwable cause) {
        super(message, cause);
    }
}
