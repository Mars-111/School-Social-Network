package ru.kors.socketbrokerservice.exceptions;

public class BadPayload extends RuntimeException {
  public BadPayload(String message) {
    super(message);
  }
}
