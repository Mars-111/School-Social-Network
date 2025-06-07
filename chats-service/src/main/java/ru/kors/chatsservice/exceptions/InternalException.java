package ru.kors.chatsservice.exceptions;

public class InternalException extends RuntimeException {
    public InternalException(String message) {
        super(message);
    }
}
