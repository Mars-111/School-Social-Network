package org.example.identityservice.exeptions;

public class NotAuthException extends RuntimeException {
    public NotAuthException(String message) {
        super(message);
    }
}
