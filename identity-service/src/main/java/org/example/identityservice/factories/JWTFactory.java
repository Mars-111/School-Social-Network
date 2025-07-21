package org.example.identityservice.factories;

import com.nimbusds.jose.*;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.identityservice.models.AccessToken;
import org.example.identityservice.models.RefreshToken;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class JWTFactory {
    private final JWSSigner signer;
    private final OctetKeyPair octetKeyPair;
    private final JwtDecoder jwtDecoder;

    public String toJWT(AccessToken tokenEntity) throws JOSEException, ParseException {
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.EdDSA)
                .keyID("Ed25519")
                .build();
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .claim("userId", tokenEntity.getUserId())
                .claim("t_type", "ACS")
                .expirationTime(Date.from(tokenEntity.getExpiresAt()))
                .build();
        SignedJWT signedJWT = new SignedJWT(header, claimsSet);
        signedJWT.sign(signer);

        return signedJWT.serialize();
    }

    public String toJWT(RefreshToken refreshTokenEntity) throws JOSEException {
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.EdDSA)
                .keyID("Ed25519")
                .build();
        if (refreshTokenEntity.getId() == null) {
            refreshTokenEntity.setId(UUID.randomUUID().toString());
            System.out.println("Refresh token ID was null, generated new ID: " + refreshTokenEntity.getId());
        }
        log.info("Access token lifetime: {}", refreshTokenEntity.getAccessTokenLifetime());
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .claim("tid", refreshTokenEntity.getId())
                .claim("client", refreshTokenEntity.getClient())
                .claim("userId", refreshTokenEntity.getUserId())
                .claim("t_type", "RFRH")
                .claim("accessTokenLifetime", refreshTokenEntity.getAccessTokenLifetime())
                .expirationTime(Date.from(refreshTokenEntity.getExpiresAt()))
                .build();
        SignedJWT signedJWT = new SignedJWT(header, claimsSet);
        signedJWT.sign(signer);
        return signedJWT.serialize();
    }
}
