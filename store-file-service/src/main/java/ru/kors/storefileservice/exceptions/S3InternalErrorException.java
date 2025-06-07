package ru.kors.storefileservice.exceptions;

public class S3InternalErrorException extends RuntimeException {
    public S3InternalErrorException(String message) {
        super(message);
    }
}
