package ru.kors.userservice.controllers.payload;

public record CreateUserPayload(
        String username,
        String firstName,
        String lastName,
        String email,
        String password
) {
    public String toBody() {
        return String.format("{" +
                "\"username\": \"%s\", " +
                "\"enabled\": true, " +
                "\"firstName\": \"%s\", " +
                "\"lastName\": \"%s\", " +
                "\"email\": \"%s\", " +
                "\"credentials\": [ { \"type\": \"password\", \"value\": \"%s\", \"temporary\": false } ]" +
                "}", username, firstName, lastName, email, password);
    }
}
