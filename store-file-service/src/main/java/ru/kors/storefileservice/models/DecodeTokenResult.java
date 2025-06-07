package ru.kors.storefileservice.models;

import java.util.Set;

public record DecodeTokenResult (
    Long userId,
    Set<Long> allowedMediaIds
) {}