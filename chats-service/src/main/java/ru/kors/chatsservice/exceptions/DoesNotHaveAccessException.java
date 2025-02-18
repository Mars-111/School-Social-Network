package ru.kors.chatsservice.exceptions;

public class DoesNotHaveAccessException extends RuntimeException {
    public DoesNotHaveAccessException(String message) {
        super(message);
    }
}
