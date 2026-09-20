package com.apitester.api_tester_backend.dto.request;

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

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder 
public class CollectionItemRequest {

    @NotBlank
    private String name;

    @NotNull
    private HttpMethod method;

    @NotBlank
    private String url;

    private Map<String, String> headers;

    private Map<String, String> queryParams;

    @Size(max = 1_000_000)
    private String body;

    private AuthType authType;

    private String bearerToken;

    private String username;

    private String password;

    private String apiKey;

    private String apiKeyName;
}