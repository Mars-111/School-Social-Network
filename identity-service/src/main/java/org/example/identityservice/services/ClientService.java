package org.example.identityservice.services;

import lombok.RequiredArgsConstructor;
import org.example.identityservice.models.entity.Client;
import org.example.identityservice.repositories.ClientRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientService {
    private final ClientRepository clientRepository;
    private final ClientCacheService clientCacheService;

    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    public Client findById(Integer id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Client not found with id: " + id));
    }

    public List<String> findAllClientIds() {
        return clientRepository.findAllClientIds();
    }

    public Client save(Client client) {
        clientCacheService.put(client);
        return clientRepository.save(client);
    }
}
