package com.matheus.shopflow.auth.service;

import com.matheus.shopflow.auth.dto.LoginRequest;
import com.matheus.shopflow.auth.dto.LoginResponse;
import com.matheus.shopflow.auth.dto.RegisterRequest;
import com.matheus.shopflow.auth.entity.Role;
import com.matheus.shopflow.auth.entity.User;
import com.matheus.shopflow.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    private PasswordEncoder passwordEncoder;
    private AuthService authService;

    @BeforeEach
    void setUp() {

        passwordEncoder =
                new BCryptPasswordEncoder();

        authService = new AuthService(
                userRepository,
                passwordEncoder,
                jwtService
        );
    }

    @Test
    void shouldRegisterUserWithEncryptedPassword() {

        RegisterRequest request = new RegisterRequest(
                "Matheus Gomes",
                "matheus@email.com",
                "senha1234"
        );

        when(userRepository.existsByEmail(request.email()))
                .thenReturn(false);

        authService.register(request);

        ArgumentCaptor<User> userCaptor =
                ArgumentCaptor.forClass(User.class);

        verify(userRepository)
                .save(userCaptor.capture());

        User savedUser = userCaptor.getValue();

        assertEquals(
                "Matheus Gomes",
                savedUser.getName()
        );

        assertEquals(
                "matheus@email.com",
                savedUser.getEmail()
        );

        assertEquals(
                Role.CUSTOMER,
                savedUser.getRole()
        );

        assertNotEquals(
                request.password(),
                savedUser.getPassword()
        );

        assertTrue(
                passwordEncoder.matches(
                        request.password(),
                        savedUser.getPassword()
                )
        );
    }

    @Test
    void shouldNotRegisterUserWhenEmailAlreadyExists() {

        RegisterRequest request = new RegisterRequest(
                "Matheus Gomes",
                "matheus@email.com",
                "senha1234"
        );

        when(userRepository.existsByEmail(request.email()))
                .thenReturn(true);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> authService.register(request)
                );

        assertEquals(
                "Email already registered",
                exception.getMessage()
        );

        verify(
                userRepository,
                never()
        ).save(any(User.class));
    }

    @Test
    void shouldAlwaysRegisterUserAsCustomer() {

        RegisterRequest request = new RegisterRequest(
                "Matheus Gomes",
                "matheus@email.com",
                "senha1234"
        );

        when(userRepository.existsByEmail(request.email()))
                .thenReturn(false);

        authService.register(request);

        ArgumentCaptor<User> userCaptor =
                ArgumentCaptor.forClass(User.class);

        verify(userRepository)
                .save(userCaptor.capture());

        assertEquals(
                Role.CUSTOMER,
                userCaptor.getValue().getRole()
        );
    }

    @Test
    void shouldLoginWithValidCredentials() {

        String rawPassword = "senha1234";

        String encodedPassword =
                passwordEncoder.encode(rawPassword);

        User user = new User(
                "Matheus Gomes",
                "matheus2@example.com",
                encodedPassword,
                Role.CUSTOMER
        );

        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        when(jwtService.generateToken(user))
                .thenReturn("generated-jwt-token");

        LoginResponse response =
                authService.login(
                        new LoginRequest(
                                user.getEmail(),
                                rawPassword
                        )
                );

        assertEquals(
                "generated-jwt-token",
                response.token()
        );

        verify(userRepository)
                .findByEmail(user.getEmail());

        verify(jwtService)
                .generateToken(user);
    }

    @Test
    void shouldRejectLoginWithInvalidPassword() {

        String rawPassword = "senha1234";
        String wrongPassword = "senhaerrada";

        String encodedPassword =
                passwordEncoder.encode(rawPassword);

        User user = new User(
                "Matheus Gomes",
                "matheus2@example.com",
                encodedPassword,
                Role.CUSTOMER
        );

        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> authService.login(
                                new LoginRequest(
                                        user.getEmail(),
                                        wrongPassword
                                )
                        )
                );

        assertEquals(
                "Invalid credentials",
                exception.getMessage()
        );

        verify(userRepository)
                .findByEmail(user.getEmail());

        verify(
                jwtService,
                never()
        ).generateToken(any(User.class));
    }
}