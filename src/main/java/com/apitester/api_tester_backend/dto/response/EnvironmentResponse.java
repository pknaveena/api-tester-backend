package com.apitester.api_tester_backend.dto.response;

import java.util.Map;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EnvironmentResponse {

    private Long id;

    private String name;

    private Map<String, String> variables;
}