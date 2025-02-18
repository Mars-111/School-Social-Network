package authenticators;

import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import jakarta.ws.rs.core.Response;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.logging.Logger;

public class CustomAuthenticator implements Authenticator {
    private static final Logger LOGGER = Logger.getLogger(CustomAuthenticator.class.getName());
    private static final String API_URL = "http://host.docker.internal:8082/internal/api/users";

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        LOGGER.info("CustomAuthenticator.authenticate");

        Response form = context.form().createForm("add-tag-register.ftl");

        context.challenge(form); // Показываем форму только один раз
    }



    @Override
    public void action(AuthenticationFlowContext context) {
        LOGGER.info("CustomAuthenticator.action called");
        String tag = context.getHttpRequest().getDecodedFormParameters().getFirst("tag");

        if (tag == null || tag.isBlank()) {
            LOGGER.warning("Tag is missing");
            sendErrorTags(context, "Tag is required");
            return;
        }

        if (isTagExists(tag)) {
            LOGGER.warning("Tag already exists: " + tag);
            sendErrorTags(context, "Tag is already taken");
            return;
        }

        if (context.getUser() == null) {
            LOGGER.severe("User is null in action()");
            sendErrorTags(context, "Authentication error. Please try again.");
            return;
        }

        if (!sendTagToServer(context.getUser().getId(), tag)) {
            LOGGER.warning("Failed to send tag to server");
            sendErrorTags(context, "Failed to process the tag. Please try again.");
            return;
        }

        context.success();
    }

    private boolean isTagExists(String tag) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + "/tag/" + tag))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200; // 200 - тег уже существует
        } catch (IOException | InterruptedException e) {
            LOGGER.severe("Error checking tag existence: " + e.getMessage());
            return false; // В случае ошибки считаем, что тег свободен
        }
    }

    private boolean sendTagToServer(String keycloakId, String tag) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            String jsonPayload = String.format("{\"keycloak_id\": \"%s\", \"tag\": \"%s\"}", keycloakId, tag);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (IOException | InterruptedException e) {
            LOGGER.severe("Error sending tag to server: " + e.getMessage());
            return false;
        }
    }

    private void sendErrorTags(AuthenticationFlowContext context, String errorMessage) {
        LOGGER.warning("Authentication failed: " + errorMessage);
        Response challenge = context.form()
                .setError(errorMessage) // <-- Показываем сообщение
                .createForm("add-tag-register.ftl");
        context.failureChallenge(AuthenticationFlowError.INVALID_CREDENTIALS, challenge);
    }


    @Override
    public boolean requiresUser() {
        return false;
    }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
        // Не требуется
    }

    @Override
    public void close() {
        // Не требуется
    }
}
