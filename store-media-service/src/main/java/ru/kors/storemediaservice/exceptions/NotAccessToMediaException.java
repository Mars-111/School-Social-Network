package ru.kors.storemediaservice.exceptions;

public class NotAccessToMediaException extends RuntimeException {
    public NotAccessToMediaException(String message) {
        super(message);
    }
}
