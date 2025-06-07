package ru.kors.chatsservice.exceptions;

public class BadFileJWT extends RuntimeException {
    public BadFileJWT(String message) {
        super(message);
    }
}
