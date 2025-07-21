package org.example.identityservice.models.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "client_access")
public class ClientAccess {
    @Id
    private String name;

    @Column(name = "access_flag", nullable = false)
    private Long accessFlag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;
}
