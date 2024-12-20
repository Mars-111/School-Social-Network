package ru.kors.webclient.config;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.AbstractUserRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
@Slf4j
class KeycloakBeansTest {
    Keycloak keycloak;




    @Test
    void keycloakIsValid() {
        keycloak = KeycloakBuilder.builder()
                .serverUrl("http://localhost:9082")
                .realm("spring")
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .clientId("admin-cli")
                .clientSecret("3q0TAMz789fiY8cbH2GWwMJMuUxHm7Sh")
//                .username("mars")
//                .password("123")
                .build();

        String username = "mars";
        Boolean exact = false;

        log.info("Searching by username: {} (exact {})", username, exact);
        List<UserRepresentation> users = keycloak.realm("spring")
                .users()
                .searchByUsername(username, exact);

        log.info("Users found by username {}", users.stream()
                .map(AbstractUserRepresentation::getUsername)
                .collect(Collectors.toList()));
    }
}











//        keycloak = KeycloakBuilder.builder()
//                .serverUrl("http://localhost:8082/auth")
//                .realm("new")
//                //.grantType(OAuth2Constants.PASSWORD)
//                .clientId("client_new")
//                .clientSecret("CrZUTO3mgrfMNUWI5KzHGiYGOQJBOKvP")
//                .username("mars")
//                .password("123")
//                .build();