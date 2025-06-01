package ru.kors.storemediaservice.exceptions;

public class BadMediaAccessJWTException extends RuntimeException {
  public BadMediaAccessJWTException(String message) {
    super(message);
  }
}
