package authenticators;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private static final String API_URL = "https://chats.mars-ssn.ru/internal/api/users";

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
            sendErrorTags(context, "Tag is required");
            return;
        }

        try {
            if (isTagExists(tag)) {
                sendErrorTags(context, "Tag is already taken");
                return;
            }
        } catch (Exception e) {
            LOGGER.severe("Error checking tag existence: " + e.getMessage());
            sendErrorTags(context, "Internal server error. Try again later.");
            return;
        }

        UserModel user = context.getUser();
        if (user == null) {
            sendErrorTags(context, "Authentication error. Please try again.");
            return;
        }

        if (!sendTagToServer(user, tag)) {
            sendErrorTags(context, "Failed to process the tag. Please try again.");
            return;
        }

        context.success();
    }

    private boolean isTagExists(String tag) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + "/tag/" + tag))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return true; // Тег существует
        } else if (response.statusCode() == 404) {
            return false; // Тег свободен
        } else {
            throw new IOException("Unexpected response code: " + response.statusCode());
        }
    }

    private boolean sendTagToServer(UserModel user, String tag) {
        String keycloakId = user.getId();
        try {
            HttpClient client = HttpClient.newHttpClient();
            String jsonPayload = String.format("{\"keycloak_id\": \"%s\", \"tag\": \"%s\"}", keycloakId, tag);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonResponse = objectMapper.readTree(response.body());

                if (jsonResponse.has("id")) {
                    JsonNode idNode = jsonResponse.get("id");
                    if (idNode.isNumber()) {
                        long userId = idNode.asLong(); // Получаем Long
                        user.setSingleAttribute("user_id", String.valueOf(userId)); // Сохраняем в Keycloak как строк
                        return true;
                    } else {
                        LOGGER.severe("Unexpected 'id' format: " + idNode.toString());
                        return false;
                    }
                } else {
                    LOGGER.severe("Server response does not contain 'id': " + response.body());
                    return false;
                }
            } else {
                LOGGER.severe("Server returned non-200 status: " + response.statusCode() + ", body: " + response.body());
                return false;
            }
        } catch (IOException | InterruptedException e) {
            LOGGER.severe("Error sending tag to server: " + e.getMessage());
            return false;
        }
    }

    private void sendErrorTags(AuthenticationFlowContext context, String errorMessage) {
        LOGGER.warning("Authentication failed: " + errorMessage);
        Response challenge = context.form()
                .setError(errorMessage)
                .createForm("add-tag-register.ftl");
        context.failureChallenge(AuthenticationFlowError.INTERNAL_ERROR, challenge);
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
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {}

    @Override
    public void close() {}
}
