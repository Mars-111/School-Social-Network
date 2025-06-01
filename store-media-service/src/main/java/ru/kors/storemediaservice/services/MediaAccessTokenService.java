package ru.kors.storemediaservice.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.kors.storemediaservice.exceptions.BadMediaAccessJWTException;
import ru.kors.storemediaservice.models.DecodeTokenResult;
import ru.kors.storemediaservice.models.FileMetadata;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MediaAccessTokenService {

    @Value("${media-service.jwt.secret}")
    private String secretKeyString;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));
    }

    public DecodeTokenResult decode(String token) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            throw new BadMediaAccessJWTException("Invalid or expired token: " + e);
        }

        if (!claims.getSubject().equals("media_access")) {
            throw new BadMediaAccessJWTException("Invalid token subject");
        }
        if (claims.get("userId") == null || claims.get("fileIds") == null) {
            throw new BadMediaAccessJWTException("Missing userId or fileIds in token");
        }

        Long userId = claims.get("userId", Number.class).longValue();
        List<Number> mediaIdsRaw = claims.get("mediaIds", List.class);
        Set<Long> mediaIds = mediaIdsRaw.stream()
                .map(Number::longValue)
                .collect(Collectors.toSet());


        return new DecodeTokenResult(userId, mediaIds);
    }

    public String generateMediaTokenForCreate(Long mediaId, Long userId, String extension, String filename, FileMetadata fileMetadata, Long fileSize) {
        String jwt = Jwts.builder()
                .subject("media_create")
                .issuer("media-service")
                .claim("userId", userId)
                .claim("mediaId", mediaId)
                .claim("size", fileSize)
                .claim("extension", extension)
                .claim("filename", filename)
                .claim("file_metadata", fileMetadata)
                .expiration(new Date(System.currentTimeMillis() + 90000)) // 90 seconds
                .signWith(secretKey)
                .compact();
        log.info("access media jwt: {}", jwt);
        return jwt;
    }

}
