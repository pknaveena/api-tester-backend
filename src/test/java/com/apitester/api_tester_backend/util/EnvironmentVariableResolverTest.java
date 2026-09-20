package com.apitester.api_tester_backend.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.apitester.api_tester_backend.exception.ApiException;

class EnvironmentVariableResolverTest {

    private EnvironmentVariableResolver resolver;

    @BeforeEach
    void setUp() {
        resolver = new EnvironmentVariableResolver();
    }

    @Test
    void shouldResolveSingleVariable() {

        Map<String, String> variables = Map.of(
                "baseUrl",
                "https://example.com");

        String result = resolver.resolve(
                "{{baseUrl}}/users",
                variables);

        assertEquals(
                "https://example.com/users",
                result);
    }

    @Test
    void shouldResolveMultipleVariables() {

        Map<String, String> variables = Map.of(
                "baseUrl", "https://example.com",
                "version", "v1");

        String result = resolver.resolve(
                "{{baseUrl}}/{{version}}/users",
                variables);

        assertEquals(
                "https://example.com/v1/users",
                result);
    }

    @Test
    void shouldThrowExceptionForUnknownVariable() {

        Map<String, String> variables = Map.of(
                "baseUrl",
                "https://example.com");

        assertThrows(
                ApiException.class,
                () -> resolver.resolve(
                        "{{unknown}}/users",
                        variables));
    }

    @Test
    void shouldReturnNullForNullInput() {

        String result = resolver.resolve(
                null,
                Map.of());

        assertNull(result);
    }

    @Test
    void shouldResolveVariableInsideBody() {

        Map<String, String> variables = Map.of(
                "token",
                "abc123");

        // "{\"token\":\"{{token}}\"}" : This is the request body
        String result = resolver.resolve(
                "{\"token\":\"{{token}}\"}",
                variables);

        assertEquals(
                "{\"token\":\"abc123\"}",
                result);
    }
}