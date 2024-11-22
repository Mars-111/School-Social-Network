package ru.kors.webclient.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.web.client.RestClient;
import ru.kors.webclient.client.UserRestClient;
import ru.kors.webclient.client.impl.UserRestClientKeycloak;
import ru.kors.webclient.security.OAuthClientHttpRequestInterceptor;

@Configuration
public class ClientBeans {
    @Bean
    public UserRestClientKeycloak userRestClientKeycloak(
            @Value("${services.user.uri:http://localhost:8081}") String catalogueBaseUri,
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientRepository authorizedClientRepository,
            @Value("${services.user.registration-id:keycloak}") String registrationId) {
        return new UserRestClientKeycloak(RestClient.builder()
                .baseUrl(catalogueBaseUri)
                .requestInterceptor(
                        new OAuthClientHttpRequestInterceptor(
                                new DefaultOAuth2AuthorizedClientManager(clientRegistrationRepository,
                                        authorizedClientRepository), registrationId))
                .build());
    }
}
