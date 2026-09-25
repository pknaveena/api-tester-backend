package com.apitester.api_tester_backend.dto.response;

import java.time.LocalDateTime;

import com.apitester.api_tester_backend.entity.enums.HttpMethod;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HistoryResponseDetailed {

    private Long id;

    private String requestId;

    private HttpMethod method;

    private String url;

    private String requestBody;

    private String requestHeaders;

    private String responseBody;

    private Integer statusCode;

    private Long responseTime;

    private Long responseSize;

    private LocalDateTime createdAt;

}