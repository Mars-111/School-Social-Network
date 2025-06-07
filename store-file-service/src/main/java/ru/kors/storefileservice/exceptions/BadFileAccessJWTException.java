package ru.kors.storefileservice.exceptions;

public class BadFileAccessJWTException extends RuntimeException {
  public BadFileAccessJWTException(String message) {
    super(message);
  }
}
