package ru.kors.socketbrokerservice.exceptions;

public class UserNotHaveThisChat extends RuntimeException {
    public UserNotHaveThisChat(String message) {
        super(message);
    }
}
