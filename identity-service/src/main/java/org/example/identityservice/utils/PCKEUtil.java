package org.example.identityservice.utils;

import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

@Slf4j
public class PCKEUtil {
    public static String generateCodeChallenge(String codeVerifier) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(codeVerifier.getBytes(StandardCharsets.US_ASCII));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
    }

    public static boolean isCodeVerifierValid(String codeVerifier, String storedCodeChallenge) throws Exception {
        String computedChallenge = generateCodeChallenge(codeVerifier);
        log.info("code verifier: " + codeVerifier);
        log.info("code challenge: " + storedCodeChallenge);
        log.info("computedChallenge: " + computedChallenge);
        return computedChallenge.equals(storedCodeChallenge);
    }

}
