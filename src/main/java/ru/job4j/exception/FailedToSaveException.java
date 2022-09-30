package ru.job4j.exception;

public class FailedToSaveException extends RuntimeException {
    public FailedToSaveException(String message) {
        super(message);
    }
}
