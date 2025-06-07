package ru.kors.storefileservice.exceptions;

public class NotAccessToFileException extends RuntimeException {
    public NotAccessToFileException(String message) {
        super(message);
    }
}
