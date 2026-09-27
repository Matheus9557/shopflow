package com.matheus.shopflow.auth.service;

import com.matheus.shopflow.auth.entity.Role;
import com.matheus.shopflow.auth.entity.User;
import com.matheus.shopflow.config.security.JwtProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class JwtServiceTest {

    private JwtEncoder jwtEncoder;
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtEncoder = mock(JwtEncoder.class);

        JwtProperties jwtProperties =
                new JwtProperties(
                        Duration.ofHours(1)
                );

        jwtService = new JwtService(
                jwtEncoder,
                jwtProperties
        );
    }

    @Test
    void shouldGenerateTokenWithUserClaims() {

        User user = new User(
                "Matheus Gomes",
                "matheus@example.com",
                "encoded-password",
                Role.CUSTOMER
        );

        Instant issuedAt = Instant.now();
        Instant expiresAt =
                issuedAt.plus(Duration.ofHours(1));

        Jwt jwt = Jwt.withTokenValue("generated-token")
                .header("alg", "RS256")
                .subject(user.getEmail())
                .claim("role", user.getRole().name())
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .build();

        when(jwtEncoder.encode(
                any(JwtEncoderParameters.class)
        )).thenReturn(jwt);

        String token = jwtService.generateToken(user);

        assertEquals(
                "generated-token",
                token
        );

        verify(jwtEncoder)
                .encode(any(JwtEncoderParameters.class));
    }
}