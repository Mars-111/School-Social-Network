package org.example.identityservice.exeptions;

public class InvalidTokenCast extends RuntimeException {
    public InvalidTokenCast(String message) {
        super(message);
    }
}
