package com.apitester.api_tester_backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.apitester.api_tester_backend.dto.request.LoginRequest;
import com.apitester.api_tester_backend.dto.request.RegisterRequest;
import com.apitester.api_tester_backend.dto.response.AuthResponse;
import com.apitester.api_tester_backend.entity.User;
import com.apitester.api_tester_backend.entity.enums.Role;
import com.apitester.api_tester_backend.exception.ApiException;
import com.apitester.api_tester_backend.repository.UserRepository;
import com.apitester.api_tester_backend.security.CustomUserDetailsService;
import com.apitester.api_tester_backend.security.JwtService;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User user;

    @BeforeEach
    void setUp() {

        registerRequest = new RegisterRequest();
        registerRequest.setName("Test User");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        user = User.builder()
                .name("Test User")
                .email("test@example.com")
                .password("encodedPassword")
                .role(Role.USER)
                .enabled(true)
                .build();
    }

    @Test
    void shouldRegisterUserSuccessfully() {

        // Arrange
        when(userRepository.existsByEmail("test@example.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("password123"))
                .thenReturn("encodedPassword");

        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        // Act
        authService.register(registerRequest);

        // Assert
        verify(userRepository)
                .existsByEmail("test@example.com");

        verify(passwordEncoder)
                .encode("password123");

        verify(userRepository)
                .save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {

        // Arrange
        when(userRepository.existsByEmail("test@example.com"))
                .thenReturn(true);

        // Act
        ApiException exception = assertThrows(
                ApiException.class,
                () -> authService.register(registerRequest));

        // Assert
        assertEquals(
                "Email already exists",
                exception.getMessage());

        assertEquals(
                HttpStatus.CONFLICT,
                exception.getStatus());

        verify(userRepository)
                .existsByEmail("test@example.com");

        verify(userRepository, never())
                .save(any(User.class));

        verify(passwordEncoder, never())
                .encode(anyString());
    }

    @Test
    void shouldLoginSuccessfully() {

        // Arrange
        UserDetails userDetails =
                org.springframework.security.core.userdetails.User
                        .withUsername("test@example.com")
                        .password("encodedPassword")
                        .roles("USER")
                        .build();

        when(userDetailsService.loadUserByUsername("test@example.com"))
                .thenReturn(userDetails);

        when(jwtService.generateToken(userDetails))
                .thenReturn("jwt-token");

        // Act
        AuthResponse response =
                authService.login(loginRequest);

        // Assert
        assertEquals("jwt-token", response.getToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(3600, response.getExpiresIn());

        verify(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        verify(userDetailsService)
                .loadUserByUsername("test@example.com");

        verify(jwtService)
                .generateToken(userDetails);
    }

    @Test
    void shouldThrowExceptionWhenLoginCredentialsAreInvalid() {

        // Arrange
        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // Act
        ApiException exception = assertThrows(
                ApiException.class,
                () -> authService.login(loginRequest));

        // Assert
        assertEquals(
                "Invalid email or password",
                exception.getMessage());

        assertEquals(
                HttpStatus.UNAUTHORIZED,
                exception.getStatus());

        verify(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        verify(userDetailsService, never())
                .loadUserByUsername(anyString());

        verify(jwtService, never())
                .generateToken(any(UserDetails.class));
    }
}