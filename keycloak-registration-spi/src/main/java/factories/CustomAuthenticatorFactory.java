package factories;

import authenticators.CustomAuthenticator;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.List;

public class    CustomAuthenticatorFactory implements AuthenticatorFactory {

    private static final String PROVIDER_ID = "custom-authenticator";

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public String getDisplayType() {
        return "Custom Authenticator with Tag";
    }

    @Override
    public String getHelpText() {
        return "Extracts the 'tag' parameter during registration.";
    }

    @Override
    public Authenticator create(KeycloakSession session) {
        return new CustomAuthenticator();
    }

    @Override
    public void init(org.keycloak.Config.Scope scope) {

    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {

    }

    @Override
    public boolean isConfigurable() {
        return false;
    }

    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return new AuthenticationExecutionModel.Requirement[]{
                AuthenticationExecutionModel.Requirement.REQUIRED,
                AuthenticationExecutionModel.Requirement.DISABLED
        };
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return null;
    }

    @Override
    public void close() {
        // Очистка ресурсов, если необходимо
    }

    @Override
    public boolean isUserSetupAllowed() {
        return false;
    }

    @Override
    public String getReferenceCategory() {
        return null;
    }
}
