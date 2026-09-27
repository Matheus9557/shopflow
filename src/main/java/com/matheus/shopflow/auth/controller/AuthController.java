package com.matheus.shopflow.auth.controller;

import com.matheus.shopflow.auth.dto.AuthenticatedUserResponse;
import com.matheus.shopflow.auth.dto.LoginRequest;
import com.matheus.shopflow.auth.dto.LoginResponse;
import com.matheus.shopflow.auth.dto.RegisterRequest;
import com.matheus.shopflow.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(
            @Valid @RequestBody RegisterRequest request
    ) {
        authService.register(request);
    }

    @PostMapping("/login")
    public LoginResponse login(
            @Valid @RequestBody LoginRequest request
    ) {
        return authService.login(request);
    }

    @GetMapping("/me")
    public AuthenticatedUserResponse me(
            Authentication authentication
    ) {

        JwtAuthenticationToken jwtAuthentication =
                (JwtAuthenticationToken) authentication;

        String email =
                jwtAuthentication.getToken().getSubject();

        String role =
                jwtAuthentication.getToken()
                        .getClaimAsString("role");

        return new AuthenticatedUserResponse(
                email,
                role
        );
    }
}