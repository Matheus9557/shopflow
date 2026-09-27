package com.matheus.shopflow.auth.service;

import com.matheus.shopflow.auth.entity.User;
import com.matheus.shopflow.config.security.JwtProperties;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final JwtProperties jwtProperties;

    public JwtService(
            JwtEncoder jwtEncoder,
            JwtProperties jwtProperties
    ) {
        this.jwtEncoder = jwtEncoder;
        this.jwtProperties = jwtProperties;
    }

    public String generateToken(User user) {

        Instant issuedAt = Instant.now();

        Instant expiresAt =
                issuedAt.plus(jwtProperties.expiration());

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(user.getEmail())
                .claim("role", user.getRole().name())
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .build();

        return jwtEncoder
                .encode(
                        JwtEncoderParameters.from(
                                claims
                        )
                )
                .getTokenValue();
    }
}