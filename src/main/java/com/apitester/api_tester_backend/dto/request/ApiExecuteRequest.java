package com.apitester.api_tester_backend.dto.request;

import java.util.Map;

import com.apitester.api_tester_backend.entity.enums.AuthType;
import com.apitester.api_tester_backend.entity.enums.HttpMethod;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class ApiExecuteRequest {

    @NotNull
    private HttpMethod method;

    @NotBlank
    @Size(max = 2000)
    private String url;

    private Map<String, String> headers;
    // Authorization → Bearer abc123
    // Content-Type → application/json

    private Map<String, String> queryParams;
    // ?page=1&limit=10

    @Size(max = 1_000_000)
    private String body;

    private Long environmentId;

    private AuthType authType;

    private String bearerToken;

    private String username;

    private String password;

    private String apiKey;

    private String apiKeyName;

    // authType = BEARER_TOKEN
    // bearerToken = abc123

    // authType = BASIC_AUTH
    // username = admin
    // password = password123

    // authType = API_KEY
    // apiKeyName = X-API-Key
    // apiKey = abc123


}