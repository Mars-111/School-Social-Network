//package org.example.identityservice.controllers.extern.security;
//
//
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//import java.util.Map;
//
//@RestController
//public class OidcDiscoveryController {
//
//    @GetMapping("/.well-known/openid-configuration")
//    public Map<String, Object> openidConfiguration() {
//        String issuer = "https://auth.mars-ssn.ru";
//
//        return Map.of(
//                "issuer", issuer,
//                "authorization_endpoint", issuer + "/authorize", // Если будет авторизация
//                "token_endpoint", issuer + "/token",
//                "jwks_uri", issuer + "/.well-known/jwks.json",
//                "response_types_supported", List.of("code", "token"),
//                "subject_types_supported", List.of("public"),
//                "id_token_signing_alg_values_supported", List.of("RS256"),
//                "scopes_supported", List.of("openid", "profile", "email"),
//                "token_endpoint_auth_methods_supported", List.of("client_secret_basic")
//        );
//    }
//}
