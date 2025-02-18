package ru.kors.chatsservice.exceptions;

public class NotAuthException extends RuntimeException {
    public NotAuthException(String message) {
        super(message);
    }
}
