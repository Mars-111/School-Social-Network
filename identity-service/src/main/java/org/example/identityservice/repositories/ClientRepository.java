package org.example.identityservice.repositories;


import org.example.identityservice.models.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Integer> {
    @Query("SELECT c.id FROM Client c")
    List<String> findAllClientIds();
}