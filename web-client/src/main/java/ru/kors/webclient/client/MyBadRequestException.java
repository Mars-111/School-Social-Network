package ru.kors.webclient.client;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class MyBadRequestException extends RuntimeException {
    private final List<String> errors;

    public MyBadRequestException(List<String> errors) {
        this.errors = errors;
    }

    public MyBadRequestException(String message, List<String> errors) {
        super(message);
        this.errors = errors;
    }

    public MyBadRequestException(String message, Throwable cause, List<String> errors) {
        super(message, cause);
        this.errors = errors;
    }

    public MyBadRequestException(Throwable cause, List<String> errors) {
        super(cause);
        this.errors = errors;
    }

    public MyBadRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, List<String> errors) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.errors = errors;
    }
}
