package org.example.identityservice.config;


import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.Ed25519Signer;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.util.Base64URL;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.util.io.pem.PemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStreamReader;
import java.util.Arrays;
import java.io.IOException;

@Configuration
@Slf4j
public class JWKConfig {

    private final String PRIVATE_PEM_PATH = "ed25519-private.pem";
    private final String PUBLIC_PEM_PATH = "ed25519-public.pem";

    private byte[] readPemBytes(String path) throws Exception {
        ClassPathResource resource = new ClassPathResource(path);
        if (!resource.exists()) {
            throw new IllegalArgumentException("PEM file not found in classpath: " + path);
        }
        try (PemReader reader = new PemReader(new InputStreamReader(resource.getInputStream()))) {
            return reader.readPemObject().getContent();
        }
    }

    private byte[] extractRawPublicKey(byte[] publicKeyBytes) {
        SubjectPublicKeyInfo spki = SubjectPublicKeyInfo.getInstance(publicKeyBytes);

        // Получаем содержимое BIT STRING (оно может уже не содержать первый байт padding)
        byte[] full = spki.getPublicKeyData().getBytes();

        int offset;
        if (full.length == 33 && full[0] == 0) {
            // формат: [pad=0][32‑байт‑ключ]
            offset = 1;
        } else if (full.length == 32) {
            // уже чистые 32 байта публичного ключа
            offset = 0;
        } else {
            throw new IllegalArgumentException(
                    "Unexpected SPKI BIT STRING length: " + full.length
            );
        }

        // Всегда возвращаем ровно 32 байта
        return Arrays.copyOfRange(full, offset, offset + 32);
    }


    private byte[] extractRawPrivateKey(byte[] privateKeyBytes) throws IOException {
        PrivateKeyInfo pki = PrivateKeyInfo.getInstance(privateKeyBytes);
        ASN1OctetString privateKey = ASN1OctetString.getInstance(pki.parsePrivateKey());
        byte[] full = privateKey.getOctets();
        return Arrays.copyOfRange(full, 0, 32);
    }

    @Bean
    public OctetKeyPair octetKeyPair() throws Exception {
        byte[] privateKeyBytes = readPemBytes(PRIVATE_PEM_PATH);
        byte[] publicKeyBytes = readPemBytes(PUBLIC_PEM_PATH);

        // Получаем 32 байта "d"
        byte[] rawPrivate = extractRawPrivateKey(privateKeyBytes);

        // Получаем 32 байта "x"
        byte[] rawPublic = extractRawPublicKey(publicKeyBytes);

        System.out.println("All bytes of private key: " + Arrays.toString(privateKeyBytes));
        System.out.println("32 bytes of private key: " + Arrays.toString(rawPrivate));

        System.out.println("All bytes of public key: " + Arrays.toString(publicKeyBytes));
        System.out.println("32 bytes of public key: " + Arrays.toString(rawPublic));

        return new OctetKeyPair.Builder(Curve.Ed25519, Base64URL.encode(rawPublic))
                .d(Base64URL.encode(rawPrivate))
//                .keyIDFromThumbprint()
                .keyID("Ed25519")
                .keyUse(KeyUse.SIGNATURE)
                .algorithm(JWSAlgorithm.EdDSA)
                .build();
    }


    @Bean
    public JWKSource<SecurityContext> jwkSource(JWKSet jwkSet) {
        log.info("JWKSet: {}", jwkSet.toJSONObject());
        return new ImmutableJWKSet<>(jwkSet);
    }

    @Bean
    public JWSSigner jwsSigner(OctetKeyPair octetKeyPair) throws Exception {
        System.out.println("Algorithm: " + octetKeyPair.getAlgorithm());
        System.out.println("Curve: " + octetKeyPair.getCurve());
        System.out.println("d (private key) length: " + octetKeyPair.getD().decode().length);
        System.out.println("x (public key) length: " + octetKeyPair.getX().decode().length);

        return new Ed25519Signer(octetKeyPair);
    }

    @Bean
    public JWKSet jwkSet(OctetKeyPair octetKeyPair) {
        OctetKeyPair publicJWK = new OctetKeyPair.Builder(Curve.Ed25519, octetKeyPair.getX())
                .keyUse(KeyUse.SIGNATURE)
                .keyID(octetKeyPair.getKeyID())
                .algorithm(JWSAlgorithm.EdDSA)
                .build();
        log.info("Public JWK: {}", publicJWK.toJSONObject());
        return new JWKSet(publicJWK);
    }
}
