package com.apitester.api_tester_backend.dto.response;

import java.util.List;
import java.util.Map;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApiExecuteResponse {

    private int statusCode;

    private String statusText;

    private Map<String, List<String>> headers;

    private String body;

    private long responseTime;

    private long responseSize;
}