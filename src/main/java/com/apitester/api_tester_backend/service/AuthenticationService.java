package com.apitester.api_tester_backend.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import com.apitester.api_tester_backend.entity.enums.AuthType;

// Instead of forcing the user to manually construct authentication headers, we provide a UI where they select 
// the authentication method, and our backend constructs the required headers automatically.
@Service
public class AuthenticationService {

    public void applyAuthentication(
            HttpHeaders headers,
            AuthType authType,
            String bearerToken,
            String username,
            String password,
            String apiKeyName,
            String apiKey) {

        if (authType == null || authType == AuthType.NONE) {
            return;
        }

        switch (authType) {

            case BEARER_TOKEN:
                headers.setBearerAuth(bearerToken);
                break;

            case BASIC_AUTH:
                applyBasicAuth(
                        headers,
                        username,
                        password);
                break;

            case API_KEY:
                headers.set(apiKeyName, apiKey);
                break;

            default:
                break;
        }
    }

    private void applyBasicAuth(
            HttpHeaders headers,
            String username,
            String password) {

        String credentials = username + ":" + password;

        String encodedCredentials = Base64.getEncoder()
                .encodeToString(
                        credentials.getBytes(
                                StandardCharsets.UTF_8));

        headers.set(
                HttpHeaders.AUTHORIZATION,
                "Basic " + encodedCredentials);
    }
}