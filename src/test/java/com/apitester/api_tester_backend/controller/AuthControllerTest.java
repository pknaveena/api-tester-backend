package com.apitester.api_tester_backend.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.apitester.api_tester_backend.dto.request.LoginRequest;
import com.apitester.api_tester_backend.dto.request.RegisterRequest;
import com.apitester.api_tester_backend.dto.response.AuthResponse;
import com.apitester.api_tester_backend.security.CustomUserDetailsService;
import com.apitester.api_tester_backend.security.JwtService;
import com.apitester.api_tester_backend.service.AuthService;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;


    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private AuthResponse authResponse;

    @BeforeEach
    void setUp() {

        registerRequest = new RegisterRequest();
        registerRequest.setName("Test User");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        authResponse = AuthResponse.builder()
                .token("jwt-token")
                .tokenType("Bearer")
                .expiresIn(3600)
                .build();
    }

    @Test
    void shouldRegisterUserSuccessfully() {

        // Act
        ResponseEntity<Void> response =
                authController.register(registerRequest);

        // Assert
        assertEquals(
                HttpStatus.CREATED,
                response.getStatusCode());

        assertNull(response.getBody());

        verify(authService)
                .register(registerRequest);
    }

    @Test
    void shouldLoginUserSuccessfully() {

        // Arrange
        when(authService.login(loginRequest))
                .thenReturn(authResponse);

        // Act
        ResponseEntity<AuthResponse> response =
                authController.login(loginRequest);

        // Assert
        assertEquals(
                HttpStatus.OK,
                response.getStatusCode());

        assertEquals(
                authResponse,
                response.getBody());

        verify(authService)
                .login(loginRequest);
    }
}

