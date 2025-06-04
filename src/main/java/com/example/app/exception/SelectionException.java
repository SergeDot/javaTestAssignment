package com.example.app.exception;

public class SelectionException extends RuntimeException {
    public SelectionException(String message) {
        super(message);
    }

    public SelectionException() {
        super("Wrong selection");
    }

    public SelectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
