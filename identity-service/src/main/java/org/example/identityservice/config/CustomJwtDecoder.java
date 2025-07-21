package org.example.identityservice.config;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.Ed25519Verifier;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

import java.text.ParseException;
import java.util.Map;


@Slf4j
public class CustomJwtDecoder implements JwtDecoder {
    private final OctetKeyPair publicJWK;

    public CustomJwtDecoder(OctetKeyPair publicJWK) {
        this.publicJWK = publicJWK.toPublicJWK();
    }


    @Override
    public Jwt decode(String token) throws JwtException {
        JWSVerifier verifier = null;
        try {
            verifier = new Ed25519Verifier(publicJWK);
        } catch (JOSEException e) {
            log.error("Error creating JWSVerifier: {}", e.getMessage());
            throw new JwtException("Invalid public jwt (public octetKeyPair): ", e);
        }
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            if (!signedJWT.verify(verifier)) {
                throw new JwtException("Invalid JWT signature");
            }

            var claimsMap = signedJWT.getJWTClaimsSet().getClaims();

            return Jwt.withTokenValue(token)
                    .headers(headers -> headers.putAll(signedJWT.getHeader().toJSONObject()))
                    .claims(claims -> claims.putAll(claimsMap))
                    .expiresAt(signedJWT.getJWTClaimsSet().getExpirationTime().toInstant())
                    .build();
        } catch (ParseException e) {
            log.error("Error parsing JWT: {}", e.getMessage());
            throw new JwtException("Invalid JWT format", e);
        } catch (JOSEException e) {
            log.error("Error verifying JWT: {}", e.getMessage());
            throw new JwtException("Invalid JWT signature", e);
        }
    }
}
