package org.example.identityservice.models;


import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.*;
import org.example.identityservice.exeptions.InvalidTokenCast;
import org.springframework.security.oauth2.jwt.Jwt;

import java.text.ParseException;
import java.time.Instant;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccessToken {
    private Long userId;
    private Instant expiresAt;

    public AccessToken(String accessJwt) throws ParseException {
        SignedJWT signedJWT = SignedJWT.parse(accessJwt);
        JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
        this.userId = claims.getLongClaim("userId");
        this.expiresAt = claims.getExpirationTime().toInstant();
    }

    public AccessToken(Jwt jwt) {
        this.userId = jwt.getClaim("userId");
        this.expiresAt = Instant.now().plusSeconds(jwt.getClaim("accessTokenLifetime"));
        if (userId == null || expiresAt == null) {
            throw new InvalidTokenCast(
                    "Invalid cast org.springframework.security.oauth2.jwt to org.example.identityservice.models.AccessToken");
        }
    }

    public AccessToken(AuthCodeData authCodeData) {
        this.userId = authCodeData.getUserId();
        this.expiresAt = Instant.now().plusSeconds(120);
    }
}
