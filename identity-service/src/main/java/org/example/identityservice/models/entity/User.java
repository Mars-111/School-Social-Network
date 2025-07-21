package org.example.identityservice.models.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users",
        indexes = {
                @Index(name = "idx_users_email", columnList = "email"),
                @Index(name = "idx_users_id_version", columnList = "id, version")
        }
)
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    private String password; //Bcrypt

    private boolean enabled = true;

    private Integer version = 0;

    @Column(name="avatar_id")
    private Long avatarFileId;

    @Column(name = "created_at")
    private Instant createdAt;

    @PrePersist
    private void setCreatedAt() {
        this.createdAt = Instant.now();
    }

    public void incrementVersion() {
        this.version++;
    }
}






//    @ElementCollection(fetch = FetchType.EAGER)
//    @CollectionTable(id = "user_roles", joinColumns = @JoinColumn(id = "user_id"))
//    @Column(id = "role")
//    private Set<String> roles = new HashSet<>();
