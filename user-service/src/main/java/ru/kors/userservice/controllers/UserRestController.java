package ru.kors.userservice.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import ru.kors.userservice.controllers.payload.CreateUserPayload;
import ru.kors.userservice.services.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("user")
@Slf4j
public class UserRestController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<String> findUsers(@RequestParam(name = "username") String username,
                                            @RequestParam(name = "exact", required = false) Boolean exact) {
        if (exact == null)
            exact = false;
        log.info("find");
        return userService.findUsers(username, exact);
    }

    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody CreateUserPayload user)  {
        log.info("create");
        return userService.createUser(user);
    }

    @PatchMapping
    public ResponseEntity<?> editUser() {
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @DeleteMapping
    public ResponseEntity<?> deleteUser() {
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
