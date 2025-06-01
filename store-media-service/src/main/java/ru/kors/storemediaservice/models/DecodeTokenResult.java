package ru.kors.storemediaservice.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

public record DecodeTokenResult (
    Long userId,
    Set<Long> allowedMediaIds
) {}