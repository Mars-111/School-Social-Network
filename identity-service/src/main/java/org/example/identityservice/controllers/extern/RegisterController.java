package org.example.identityservice.controllers.extern;

import lombok.RequiredArgsConstructor;
import org.example.identityservice.controllers.extern.dto.CreateUserDTO;
import org.example.identityservice.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/register")
@RequiredArgsConstructor
public class RegisterController {

    private final UserService userService;

    @PostMapping
    @CrossOrigin(origins = "https://mars-ssn.ru", methods = RequestMethod.POST)
    public ResponseEntity<?> register(@RequestBody CreateUserDTO createUserDTO) {
        return ResponseEntity.ok(userService.create(createUserDTO));
    }
}
