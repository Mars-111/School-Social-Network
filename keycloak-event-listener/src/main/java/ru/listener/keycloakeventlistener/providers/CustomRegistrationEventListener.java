package ru.listener.keycloakeventlistener.providers;

import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;


public class CustomRegistrationEventListener implements EventListenerProvider {

    private final KeycloakSession session;
    private Logger log = Logger.getLogger(CustomRegistrationEventListener.class.getName());

    public CustomRegistrationEventListener(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public void onEvent(Event event) {
        if (event.getType() == EventType.REGISTER) {
            String username = event.getDetails().get("username");
            if (username != null) {
                log.info("User " + username + " registered");
                sendPostRequest(username);
            }
        }
    }

    @Override
    public void onEvent(AdminEvent adminEvent, boolean b) {

    }

    private void sendPostRequest(String username) {
        try {
            URL url = new URL("https://chats.mars-ssn.ru/internal/api/users");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");

            String json = "{\"username\":\"" + username + "\"}";

            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = conn.getResponseCode();
            // можно залогировать responseCode для диагностики

            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {}
}
