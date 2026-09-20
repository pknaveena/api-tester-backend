package com.apitester.api_tester_backend.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.apitester.api_tester_backend.dto.request.ApiExecuteRequest;
import com.apitester.api_tester_backend.entity.enums.AuthType;
import com.apitester.api_tester_backend.exception.ApiException;

class ApiRequestValidatorTest {

    private ApiRequestValidator validator;

    @BeforeEach
    void setUp() {
        validator = new ApiRequestValidator();
    }

    @Test
    void shouldAcceptValidRequest() {

        ApiExecuteRequest request =
                new ApiExecuteRequest();

        request.setUrl("https://example.com");
        request.setHeaders(
                Map.of("Content-Type", "application/json")
        );
        request.setQueryParams(
                Map.of("page", "1")
        );

        assertDoesNotThrow(() ->
                validator.validate(request)
        );
    }

    @Test
    void shouldRejectTooManyHeaders() {

        Map<String, String> headers =
                new HashMap<>();

        for (int i = 0; i < 51; i++) {
            headers.put(
                    "Header-" + i,
                    "value"
            );
        }

        ApiExecuteRequest request =
                new ApiExecuteRequest();

        request.setHeaders(headers);

        assertThrows(
                ApiException.class,
                () -> validator.validate(request)
        );
    }

    @Test
    void shouldRejectTooManyQueryParameters() {

        Map<String, String> queryParams =
                new HashMap<>();

        for (int i = 0; i < 51; i++) {
            queryParams.put(
                    "param-" + i,
                    "value"
            );
        }

        ApiExecuteRequest request =
                new ApiExecuteRequest();

        request.setQueryParams(queryParams);

        assertThrows(
                ApiException.class,
                () -> validator.validate(request)
        );
    }

    @Test
    void shouldRejectMissingBearerToken() {

        ApiExecuteRequest request =
                new ApiExecuteRequest();

        request.setAuthType(
                AuthType.BEARER_TOKEN
        );

        request.setBearerToken(null);

        assertThrows(
                ApiException.class,
                () -> validator.validate(request)
        );
    }

    @Test
    void shouldRejectMissingBasicAuthCredentials() {

        ApiExecuteRequest request =
                new ApiExecuteRequest();

        request.setAuthType(
                AuthType.BASIC_AUTH
        );

        request.setUsername(null);
        request.setPassword(null);

        assertThrows(
                ApiException.class,
                () -> validator.validate(request)
        );
    }

    @Test
    void shouldRejectMissingApiKey() {

        ApiExecuteRequest request =
                new ApiExecuteRequest();

        request.setAuthType(
                AuthType.API_KEY
        );

        request.setApiKeyName(null);
        request.setApiKey(null);

        assertThrows(
                ApiException.class,
                () -> validator.validate(request)
        );
    }
}