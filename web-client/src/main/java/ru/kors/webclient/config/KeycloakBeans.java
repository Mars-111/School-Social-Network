//package ru.kors.webclient.config;
//
//import lombok.extern.slf4j.Slf4j;
//import org.keycloak.OAuth2Constants;
//import org.keycloak.admin.client.Keycloak;
//import org.keycloak.admin.client.KeycloakBuilder;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//@Slf4j
//public class KeycloakBeans {
//    @Bean
//    public Keycloak keycloak(
//            @Value("${keycloak.admin.client.serverUrl:http://localhost:8082}") String serverUrl,
//            @Value("${keycloak.admin.client.realm:school-social-network}") String realm,
//            @Value("${keycloak.admin.client.clientId:web-client}") String clientId,
//            @Value("${keycloak.admin.client.username:admin}") String username,
//            @Value("${keycloak.admin.client.password:admin}") String password,
//            @Value("${keycloak.admin.client.clientSecret}") String clientSecret) {
//            log.info("\nserverUrl: {},\nrealm: {},\nclientId: {},\nclientSecret: {},\nusername: {},\npassword: {}",
//                    serverUrl + "/auth", realm, clientId, clientSecret, username, password);
//        return KeycloakBuilder.builder()
//                .serverUrl(serverUrl + "/auth")
//                .realm(realm)
//                .grantType(OAuth2Constants.PASSWORD)
//                .clientId(clientId)
//                .clientSecret(clientSecret)
//                .username(username)
//                .password(password)
//                .build();
//    }
//}
