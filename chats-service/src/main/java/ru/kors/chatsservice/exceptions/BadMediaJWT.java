package ru.kors.chatsservice.exceptions;

public class BadMediaJWT extends RuntimeException {
    public BadMediaJWT(String message) {
        super(message);
    }
}
