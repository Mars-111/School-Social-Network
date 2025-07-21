package org.example.identityservice.services;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.example.identityservice.models.entity.Client;
import org.example.identityservice.repositories.ClientRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class ClientCacheService {

    private final ClientRepository clientRepository;

    private final Map<String, Client> clientMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        List<Client> clients = clientRepository.findAll();
        for (Client client : clients) {
            clientMap.put(client.getId(), client);
        }
        System.out.println("✅ Кеш клиентов инициализирован: " + clientMap.size());
    }

    public Client getById(String id) {
        return clientMap.getOrDefault(id, null);
    }

    public Collection<Client> getAll() {
        return clientMap.values();
    }

    public void put(Client client) {
        clientMap.put(client.getId(), client);
    }
}

