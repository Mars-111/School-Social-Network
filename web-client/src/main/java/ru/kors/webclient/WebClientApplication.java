package ru.kors.webclient;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.RealmRepresentation;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WebClientApplication {

    public static void main(String[] args) {

        SpringApplication.run(WebClientApplication.class, args);
    }

}
