//package ru.kors.chatsservice.services;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.security.Keys;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.test.util.ReflectionTestUtils;
//
//import javax.crypto.SecretKey;
//import java.nio.charset.StandardCharsets;
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class FileJWTServiceTest {
//
//    private FileJWTService jwtService;
//    private final String testSecret = "very_secure_and_long_secret_key_1234567890"; // минимум 32 символа
//    private final String testIssuer = "chat-service";
//
//    @BeforeEach
//    void setUp() {
//        jwtService = new FileJWTService();
//        ReflectionTestUtils.setField(jwtService, "secretKey", testSecret);
//        ReflectionTestUtils.setField(jwtService, "issuer", testIssuer);
//    }
//
//    @Test
//    void generateFileAccessToken_shouldReturnValidToken() {
//        Set<Long> fileIds = new HashSet<>(Arrays.asList(101L, 102L));
//        Long userId = 42L;
//        long expiration = 3600; // 1 час
//
//        String token = jwtService.generateFileAccessToken(fileIds, userId, expiration);
//        assertNotNull(token);
//
//        SecretKey key = Keys.hmacShaKeyFor(testSecret.getBytes(StandardCharsets.UTF_8));
//        Claims claims = Jwts.parser()
//                .verifyWith(key)
//                .build()
//                .parseSignedClaims(token)
//                .getPayload();
//
//        assertEquals("media_access", claims.getSubject());
//        assertEquals(testIssuer, claims.getIssuer());
//        assertEquals(userId.intValue(), ((Number) claims.get("userId")).intValue());
//
//        List<Integer> extractedFileIds = (List<Integer>) claims.get("fileId");
//        assertTrue(extractedFileIds.contains(101));
//        assertTrue(extractedFileIds.contains(102));
//
//        assertTrue(claims.getExpiration().after(new Date()));
//        assertNotNull(claims.getIssuedAt());
//    }
//}
