package com.apitester.api_tester_backend.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResponse {

    private String token;

    private String tokenType;

    private long expiresIn;
}
