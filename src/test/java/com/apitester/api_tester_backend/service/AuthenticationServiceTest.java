package com.apitester.api_tester_backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import com.apitester.api_tester_backend.entity.enums.AuthType;

class AuthenticationServiceTest {

    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        authenticationService = new AuthenticationService();
    }

    @Test
    void shouldApplyBearerAuthentication() {

        HttpHeaders headers = new HttpHeaders();

        authenticationService.applyAuthentication(
                headers,
                AuthType.BEARER_TOKEN,
                "abc123",
                null,
                null,
                null,
                null);

        assertEquals(
                "Bearer abc123",
                headers.getFirst(
                        HttpHeaders.AUTHORIZATION));
    }

    @Test
    void shouldApplyBasicAuthentication() {

        HttpHeaders headers = new HttpHeaders();

        authenticationService.applyAuthentication(
                headers,
                AuthType.BASIC_AUTH,
                null,
                "admin",
                "password",
                null,
                null);

        String authorization = headers.getFirst(
                HttpHeaders.AUTHORIZATION);

        //Base64-encode that string: 
        // admin:password
        // ↓ Base64
        // YWRtaW46cGFzc3dvcmQ=
        assertEquals(
                "Basic YWRtaW46cGFzc3dvcmQ=",
                authorization);
    }

    @Test
    void shouldApplyApiKeyAuthentication() {

        HttpHeaders headers = new HttpHeaders();

        authenticationService.applyAuthentication(
                headers,
                AuthType.API_KEY,
                null,
                null,
                null,
                "X-API-Key",
                "abc123");

        assertEquals(
                "abc123",
                headers.getFirst("X-API-Key"));
    }

    @Test
    void shouldNotAddAuthenticationForNone() {

        HttpHeaders headers = new HttpHeaders();

        authenticationService.applyAuthentication(
                headers,
                AuthType.NONE,
                null,
                null,
                null,
                null,
                null);

        assertFalse(
        headers.containsHeader(HttpHeaders.AUTHORIZATION)
);
    }
}