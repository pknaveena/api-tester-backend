package com.apitester.api_tester_backend.dto.response;

import java.time.LocalDateTime;

import com.apitester.api_tester_backend.entity.enums.HttpMethod;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HistoryResponse {

    private Long id;

    private String requestId;

    private HttpMethod method;

    private String url;

    private Integer statusCode;

    private Long responseTime;

    private Long responseSize;

    private LocalDateTime createdAt;

    // we don't initially return: requestBody,requestHeaders ,responseBody 
    // Returning every large request/response body for the list would be inefficient.
}