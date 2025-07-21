package org.example.identityservice.models;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.*;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;

import java.text.ParseException;
import java.time.Instant;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RefreshToken {
    private String id;
    private String client;
    private Long userId;
    private Integer accessTokenLifetime = 120; // Default lifetime in seconds
    private Instant expiresAt;

    public RefreshToken(String refreshJwt) throws ParseException {
        SignedJWT signedJWT = SignedJWT.parse(refreshJwt);
        JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
        this.id = claims.getStringClaim("tid");
        this.userId = claims.getLongClaim("userId");
        this.client = claims.getStringClaim("clientId");
        this.expiresAt = claims.getExpirationTime().toInstant();
    }

    public RefreshToken(Jwt jwt) {
        this.id = jwt.getClaimAsString("tid");
        System.out.println("RefreshToken tid: inJwt=" + jwt.getClaimAsString("tid") + " this=" + this.id);

        this.client = jwt.getClaim("client");
        System.out.println("RefreshToken client: inJwt=" + jwt.getClaim("client") + " this=" + this.client);

        this.userId = (Long) jwt.getClaim("userId");
        System.out.println("RefreshToken userId: inJwt=" + jwt.getClaim("userId") + " this=" + this.userId);

        this.expiresAt = jwt.getClaimAsInstant("exp");
        System.out.println("RefreshToken exp: inJwt=" + jwt.getClaimAsInstant("exp") + " this=" + this.expiresAt);

        if (id == null || client == null || userId == null || expiresAt == null) {
            throw new BadJwtException("Refresh token does not contain all fields: " + jwt.getTokenValue());
        }
    }

    public RefreshToken(AuthCodeData authCodeData) {
        this.client = authCodeData.getClientId();
        this.userId = authCodeData.getUserId();
    }
}
