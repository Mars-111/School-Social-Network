package ru.listener.keycloakeventlistener.factories;

import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import ru.listener.keycloakeventlistener.providers.CustomRegistrationEventListener;

public class CustomRegistrationEventListenerFactory implements EventListenerProviderFactory {

    @Override
    public EventListenerProvider create(KeycloakSession session) {
        return new CustomRegistrationEventListener(session);
    }

    @Override
    public void init(org.keycloak.Config.Scope config) {
        // можно парсить конфиги из keycloak.conf (если нужно)
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {}

    @Override
    public void close() {}

    @Override
    public String getId() {
        return "custom-registration-listener";
    }
}
