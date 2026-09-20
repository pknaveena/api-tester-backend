package com.apitester.api_tester_backend.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.apitester.api_tester_backend.exception.ApiException;

class UrlValidatorTest {

    private UrlValidator urlValidator;

    @BeforeEach
    void setUp() {
        urlValidator = new UrlValidator();
    }

    @Test
    void shouldAcceptValidHttpsUrl() {

        assertDoesNotThrow(() ->
                urlValidator.validate("https://example.com")
        );
    }

    @Test
    void shouldAcceptValidHttpUrl() {

        assertDoesNotThrow(() ->
                urlValidator.validate("http://example.com")
        );
    }

    @Test
    void shouldRejectUnsupportedProtocol() {

        assertThrows(
                ApiException.class,
                () -> urlValidator.validate("ftp://example.com")
        );
    }

    @Test
    void shouldRejectLocalhost() {

        assertThrows(
                ApiException.class,
                () -> urlValidator.validate("http://localhost:8080")
        );
    }

    @Test
    void shouldRejectInvalidUrl() {

        assertThrows(
                ApiException.class,
                () -> urlValidator.validate("invalid-url")
        );
    }
}