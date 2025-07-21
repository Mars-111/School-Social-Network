package org.example.identityservice.controllers.extern.security;


import com.nimbusds.jose.jwk.JWKSet;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@Getter
@RestController
@RequiredArgsConstructor
public class JWKsController {

    private final JWKSet jwkSet;

    @GetMapping("/.well-known/jwks.json")
    public Map<String, Object> getJwks() {
        return jwkSet.toJSONObject();
    }

}