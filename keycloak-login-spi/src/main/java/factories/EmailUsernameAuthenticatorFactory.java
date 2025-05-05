package factories;

import authenticators.EmailUsernameFormAuthenticator;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.List;

public class EmailUsernameAuthenticatorFactory implements AuthenticatorFactory {

    public static final String ID = "email-login-authenticator";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getDisplayType() {
        return "Email Login Authenticator";
    }

    @Override
    public String getReferenceCategory() {
        return null;
    }

    @Override
    public Authenticator create(KeycloakSession session) {
        return new EmailUsernameFormAuthenticator();
    }

    @Override public boolean isConfigurable() { return false; }

    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return new AuthenticationExecutionModel.Requirement[0];
    }

    @Override
    public boolean isUserSetupAllowed() {
        return false;
    }

    @Override public void init(org.keycloak.Config.Scope config) {}
    @Override public void postInit(KeycloakSessionFactory factory) {}
    @Override public void close() {}
    @Override public String getHelpText() { return "Login using email only"; }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return List.of();
    }
}
