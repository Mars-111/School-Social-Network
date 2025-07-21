package org.example.identityservice.config;

import com.nimbusds.jose.jwk.OctetKeyPair;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;

@Configuration
public class JwtDecoderBean {
    @Bean
    public JwtDecoder jwtDecoder(OctetKeyPair octetKeyPair) {
        return new CustomJwtDecoder(octetKeyPair.toPublicJWK());
    }
}
