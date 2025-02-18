package ru.listener.keycloakeventlistener.factories;

import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.Config.Scope;
import ru.listener.keycloakeventlistener.listeners.CustomEventListenerProvider;

public class CustomEventListenerProviderFactory implements EventListenerProviderFactory {

    @Override
    public EventListenerProvider create(KeycloakSession keycloakSession) {
        return new CustomEventListenerProvider();
    }

    @Override
    public void init(Scope scope) {
        // Initialization logic if needed
    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {
        // Post initialization logic if needed
    }

    @Override
    public void close() {
        // Cleanup logic if needed
    }

    @Override
    public String getId() {
        return "custom-event-listener";
    }
}