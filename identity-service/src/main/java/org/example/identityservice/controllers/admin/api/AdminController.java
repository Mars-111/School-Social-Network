package org.example.identityservice.controllers.admin.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.identityservice.controllers.admin.api.dto.CreateClientDTO;
import org.example.identityservice.models.entity.Client;
import org.example.identityservice.services.ClientService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/api")
@RequiredArgsConstructor
@Slf4j
public class AdminController {
    private final ClientService clientService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/clients")
    public List<Client> getAllClients() {
        return clientService.findAll();
    }

    @PostMapping("/clients")
    public ResponseEntity<?> createClient(@RequestBody @Valid CreateClientDTO createClientDTO) {
        Client client = new Client();
        client.setId(createClientDTO.id());
        client.setDescription(createClientDTO.description());
        client.setAllowedRedirectUris(createClientDTO.redirectUris());

        // Хэширование секрета (если есть)
        if (createClientDTO.secret() != null && !createClientDTO.secret().isBlank()) {
            client.setSecret(passwordEncoder.encode(createClientDTO.secret()));
        }

        try {
            client = clientService.save(client);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Client with this id already exists"));
        } catch (Exception e) {
            log.error("Error creating client", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Internal server error"));
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(client.getId());

    }
}
