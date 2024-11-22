package ru.kors.userservice.services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.kors.userservice.controllers.payload.CreateUserPayload;

public interface UserService {
    ResponseEntity<String> findUsers(String username, Boolean exact);
    ResponseEntity<String> findUsersByEmail(String email, Boolean exact);
    ResponseEntity<String> createUser(CreateUserPayload user);
}
