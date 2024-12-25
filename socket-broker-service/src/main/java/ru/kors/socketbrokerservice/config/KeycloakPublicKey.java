package ru.kors.socketbrokerservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
@Slf4j
public class KeycloakPublicKey {

    @Value("${keycloak.public.key.n}")
    private String n;

    @Value("${keycloak.public.key.e}")
    private String e;

    @Bean
    public PublicKey publicKey() throws Exception {
        log.info("Public Key N: {}", n);
        log.info("Public Key E: {}", e);

        BigInteger modulus = new BigInteger(1, Base64.getUrlDecoder().decode(n));
        BigInteger exponent = new BigInteger(1, Base64.getUrlDecoder().decode(e));

        // Создаем спецификацию публичного ключа
        RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, exponent);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        // Генерируем публичный ключ
        return keyFactory.generatePublic(spec);
    }

}
