package ru.kors.userservice;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class UserServiceApplication {

    public static void main(String[] args) {
        Keycloak keycloak = Keycloak.getInstance(
                "http://localhost:8080/auth",
                "school-social-network",
                "admin",
                "admin",
                "user-service");

        UsersResource usersResource = keycloak.realm("school-social-network").users();

        List<UserRepresentation> allUsers = usersResource.list();

        SpringApplication.run(UserServiceApplication.class, args);
    }

}
