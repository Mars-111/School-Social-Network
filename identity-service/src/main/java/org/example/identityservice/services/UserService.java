package org.example.identityservice.services;

import lombok.RequiredArgsConstructor;
import org.example.identityservice.controllers.extern.dto.CreateUserDTO;
import org.example.identityservice.exeptions.NoSuchUserException;
import org.example.identityservice.models.UserIdAndPassword;
import org.example.identityservice.models.UserInfo;
import org.example.identityservice.models.entity.User;
import org.example.identityservice.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Long getUserIdIfExistsByUsernameAndPassword(String username, String password) {
        UserIdAndPassword userIdAndPassword = userRepository.findIdAndPasswordByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + username));
        if (!passwordEncoder.matches(password, userIdAndPassword.getPassword())) {
            throw new IllegalArgumentException("Invalid password for user: " + username);
        }
        return userIdAndPassword.getId();
    }

    public Long getUserIdIfExistsByEmailAndPassword(String email, String password) {
        UserIdAndPassword userIdAndPassword = userRepository.findIdAndPasswordByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
        if (!passwordEncoder.matches(password, userIdAndPassword.getPassword())) {
            throw new IllegalArgumentException("Invalid password for user: " + email);
        }
        return userIdAndPassword.getId();
    }

    public User create(CreateUserDTO createUserDTO) {
        User user = new User();
        user.setUsername(createUserDTO.username());
        user.setEmail(createUserDTO.email());
        user.setPassword(passwordEncoder.encode(createUserDTO.password()));
        return userRepository.save(user);
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));
    }

    public UserInfo getUserInfoById(Long id) {
        return userRepository.findInfoById(id)
                .orElseThrow(() -> new NoSuchUserException("User not found with ID: " + id));
    }

    public boolean existsByIdAndVersion(Long id, Integer version) {
        return userRepository.existsByIdAndVersion(id, version);
    }

    public User save(User user) {
        return userRepository.save(user);
    }
}
