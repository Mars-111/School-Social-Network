package authenticators;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import org.keycloak.authentication.*;
import org.keycloak.models.*;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.services.managers.AuthenticationManager;
import org.keycloak.services.messages.Messages;
import org.keycloak.services.validation.Validation;


public class EmailUsernameFormAuthenticator implements Authenticator {

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        Response challenge = context.form()
                .createForm("login.ftl"); // Используй стандартную форму или свою
        context.challenge(challenge);
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        String email = formData.getFirst(AuthenticationManager.FORM_USERNAME);
        String password = formData.getFirst(CredentialRepresentation.PASSWORD);

        if (Validation.isBlank(email) || Validation.isBlank(password)) {
            context.form().setError(Messages.MISSING_USERNAME);
            context.failureChallenge(AuthenticationFlowError.INVALID_USER,
                    context.form().createErrorPage(Response.Status.BAD_REQUEST));
            return;
        }

        UserModel user = context.getSession().users().getUserByEmail(context.getRealm(), email);
        if (user == null) {
            context.form().setError(Messages.INVALID_USER);
            context.failureChallenge(AuthenticationFlowError.INVALID_USER,
                    context.form().createErrorPage(Response.Status.UNAUTHORIZED));
            return;
        }

        context.setUser(user);
        context.success();
    }

    @Override public void close() {}
    @Override public boolean requiresUser() { return false; }
    @Override public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) { return true; }
    @Override public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {}
}
