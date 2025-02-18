package ru.kors.chatsservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kors.chatsservice.models.entity.User;


import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByKeycloakId(String keycloakId);
    void deleteUserByKeycloakId(String keycloakId);

    Optional<User> findByTag(String tag);

    Boolean existsByTag(String tag);
}
