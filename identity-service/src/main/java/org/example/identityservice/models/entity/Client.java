package org.example.identityservice.models.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.identityservice.models.entity.converters.RedirectUriConverter;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "clients")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Client {
    @Id
    @Column(unique = true, nullable = false)
    private String id;

    @Basic(fetch = FetchType.LAZY)
    @Column(nullable = true)
    private String description;

    @Column(nullable = true)
    private String secret; // Bcrypt

    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.DETACH}, mappedBy = "client")
    private Set<ClientAccess> clientAccess;

    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;

    @Convert(converter = RedirectUriConverter.class)
    @Column(name = "allowed_redirect_uris", nullable = true)
    private List<String> allowedRedirectUris;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    private void setCreatedAt() {
        this.createdAt = Instant.now();
    }
}
