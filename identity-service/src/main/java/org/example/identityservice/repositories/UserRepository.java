package org.example.identityservice.repositories;

import org.example.identityservice.models.UserIdAndPassword;
import org.example.identityservice.models.UserInfo;
import org.example.identityservice.models.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsernameAndPassword(String username, String password);
    boolean existsByEmailAndPassword(String email, String password);

    @Query("SELECT u.id AS id, u.password AS password FROM User u WHERE u.username = ?1")
    Optional<UserIdAndPassword> findIdAndPasswordByUsername(String username);
    @Query("SELECT u.id AS id, u.password AS password FROM User u WHERE u.email = ?1")
    Optional<UserIdAndPassword> findIdAndPasswordByEmail(String email);

    @Query("""
       SELECT u.id AS id,
              u.username AS username,
              u.enabled AS enabled,
              u.createdAt AS createdAt
       FROM User u
       WHERE u.id = ?1
       """)
    Optional<UserInfo> findInfoById(Long id);

    boolean existsByIdAndVersion(Long id, Integer version);
}
