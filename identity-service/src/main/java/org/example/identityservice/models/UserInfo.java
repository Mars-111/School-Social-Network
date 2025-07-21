package org.example.identityservice.models;

import java.time.Instant;

public interface UserInfo {
    Long getId();
    String getUsername();
    boolean isEnabled();
    Instant getCreatedAt(); // Или LocalDateTime, если нужно
}
