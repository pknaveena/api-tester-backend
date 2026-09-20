package com.apitester.api_tester_backend.dto.response;

import java.util.Map;

import com.apitester.api_tester_backend.entity.enums.AuthType;
import com.apitester.api_tester_backend.entity.enums.HttpMethod;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CollectionItemResponse {

    private Long id;
    private String name;
    private HttpMethod method;
    private String url;
    private Map<String, String> headers;
    private Map<String, String> queryParams;
    private String body;
    private AuthType authType;

    private String bearerToken;

    private String username;

    private String password;

    private String apiKey;

    private String apiKeyName;
}