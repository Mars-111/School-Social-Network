//package ru.kors.webclient.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import jakarta.ws.rs.core.Response;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import net.minidev.json.JSONArray;
//import net.minidev.json.JSONObject;
//import org.keycloak.OAuth2Constants;
//import org.keycloak.admin.client.Keycloak;
//import org.keycloak.admin.client.KeycloakBuilder;
//import org.keycloak.admin.client.resource.UsersResource;
//import org.keycloak.representations.idm.RealmRepresentation;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Controller
//@RequiredArgsConstructor
//@RequestMapping("users")
//@Slf4j
//public class UsersController {
//    @Value("${keycloak.admin.client.realm:school-social-network}")
//    String keycloakRealm;
//    @Autowired
//    Keycloak keycloak;
//
//    @GetMapping()
//    public String findUsers(Model model,
//                                  @RequestParam(name = "username", required = false) String username,
//                                  @RequestParam(name = "exact", required = false) Boolean exact) {
//        log.info("count: {}", keycloak.realm("keycloakRealm").users().count());
//        RealmRepresentation realm = keycloak.realm(keycloakRealm).toRepresentation();
//        log.info(keycloak.serverInfo().toString());
//        UsersResource usersResource = keycloak.realm(keycloakRealm).users();
//        log.info("UsersResource: {}", usersResource);
//        log.info("realm.getUsers();: {}", realm.getUsers());
//
//
//
//
//
//
//
//        if (exact != null)
//            model.addAttribute("exact", exact);
//        if (username != null && !username.isEmpty()) {
//            model.addAttribute("username", username);
//            model.addAttribute("findUsers", usersResource.searchByUsername(username, exact));
//        }
//        else {
//            model.addAttribute("findUsers", usersResource.searchByUsername("", false));
//        }
//
//        return "list-users";
//    }
//
////    @PostMapping
////    public ResponseEntity<String> createUser(@RequestBody CreateUserPayload user)  {
////        log.info("create");
////        return userService.createUser(user);
////    }
//}
