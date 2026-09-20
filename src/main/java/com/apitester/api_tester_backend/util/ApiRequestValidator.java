package com.apitester.api_tester_backend.util;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.apitester.api_tester_backend.dto.request.ApiExecuteRequest;
import com.apitester.api_tester_backend.entity.enums.AuthType;
import com.apitester.api_tester_backend.exception.ApiException;

@Component
public class ApiRequestValidator {

    public void validate(ApiExecuteRequest request) {

        validateHeaders(request.getHeaders());

        validateQueryParams(request.getQueryParams());

        validateAuthentication(request);
    }

    private void validateHeaders(
            Map<String, String> headers) {

        if (headers == null) {
            return;
        }

        if (headers.size() > 50) {
            throw new ApiException(
                    "Too many headers",
                    HttpStatus.BAD_REQUEST);
        }

    validateHeaderNames(headers);
    validateHeaderValues(headers);
    }

    private void validateHeaderNames(
        Map<String, String> headers) {

    for (String name : headers.keySet()) {

        if (name == null || name.isBlank()) {
            throw new ApiException(
                    "Header name cannot be empty",
                    HttpStatus.BAD_REQUEST);
        }

        if (name.length() > 200) {
            throw new ApiException(
                    "Header name is too long",
                    HttpStatus.BAD_REQUEST);
        }
    }
}

private void validateHeaderValues(
        Map<String, String> headers) {

    for (String value : headers.values()) {

        if (value != null && value.length() > 10_000) {
            throw new ApiException(
                    "Header value is too long",
                    HttpStatus.BAD_REQUEST);
        }
    }
}

    private void validateQueryParams(
            Map<String, String> queryParams) {

        if (queryParams == null) {
            return;
        }

        if (queryParams.size() > 50) {
            throw new ApiException(
                    "Too many query parameters",
                    HttpStatus.BAD_REQUEST);
        }
    }

    private void validateAuthentication(
            ApiExecuteRequest request) {

        AuthType authType = request.getAuthType();

        if (authType == null
                || authType == AuthType.NONE) {
            return;
        }

        switch (authType) {

            case BEARER_TOKEN:

                if (request.getBearerToken() == null
                        || request.getBearerToken().isBlank()) {

                    throw new ApiException(
                            "Bearer token is required",
                            HttpStatus.BAD_REQUEST);
                }

                break;

            case BASIC_AUTH:

                if (request.getUsername() == null
                        || request.getUsername().isBlank()
                        || request.getPassword() == null
                        || request.getPassword().isBlank()) {

                    throw new ApiException(
                            "Username and password are required",
                            HttpStatus.BAD_REQUEST);
                }

                break;

            case API_KEY:

                if (request.getApiKeyName() == null
                        || request.getApiKeyName().isBlank()
                        || request.getApiKey() == null
                        || request.getApiKey().isBlank()) {

                    throw new ApiException(
                            "API key name and value are required",
                            HttpStatus.BAD_REQUEST);
                }

                break;

            default:
                break;
        }
    }

    
    
}
