package com.matheus.shopflow.auth.service;

import com.matheus.shopflow.auth.dto.LoginRequest;
import com.matheus.shopflow.auth.dto.LoginResponse;
import com.matheus.shopflow.auth.dto.RegisterRequest;
import com.matheus.shopflow.auth.entity.Role;
import com.matheus.shopflow.auth.entity.User;
import com.matheus.shopflow.auth.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public void register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException(
                    "Email already registered"
            );
        }

        String encodedPassword =
                passwordEncoder.encode(request.password());

        User user = new User(
                request.name(),
                request.email(),
                encodedPassword,
                Role.CUSTOMER
        );

        userRepository.save(user);
    }

    public LoginResponse login(LoginRequest request) {

        User user = userRepository
                .findByEmail(request.email())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Invalid credentials"
                        )
                );

        if (!passwordEncoder.matches(
                request.password(),
                user.getPassword()
        )) {
            throw new IllegalArgumentException(
                    "Invalid credentials"
            );
        }

        String token =
                jwtService.generateToken(user);

        return new LoginResponse(token);
    }
}