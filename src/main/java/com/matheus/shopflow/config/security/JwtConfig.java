package com.matheus.shopflow.config.security;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class JwtConfig {

    @Bean
    public KeyPair jwtKeyPair() {
        try {
            KeyPairGenerator generator =
                    KeyPairGenerator.getInstance("RSA");

            generator.initialize(2048);

            return generator.generateKeyPair();

        } catch (Exception ex) {
            throw new IllegalStateException(
                    "Could not generate JWT key pair",
                    ex
            );
        }
    }

    @Bean
    public JwtEncoder jwtEncoder(KeyPair jwtKeyPair) {

        RSAPublicKey publicKey =
                (RSAPublicKey) jwtKeyPair.getPublic();

        RSAPrivateKey privateKey =
                (RSAPrivateKey) jwtKeyPair.getPrivate();

        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .build();

        JWKSource<SecurityContext> jwkSource =
                new ImmutableJWKSet<>(
                        new JWKSet(rsaKey)
                );

        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    public JwtDecoder jwtDecoder(KeyPair jwtKeyPair) {

        RSAPublicKey publicKey =
                (RSAPublicKey) jwtKeyPair.getPublic();

        return NimbusJwtDecoder
                .withPublicKey(publicKey)
                .build();
    }
}