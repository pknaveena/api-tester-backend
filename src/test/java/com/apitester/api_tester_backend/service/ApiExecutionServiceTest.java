package com.apitester.api_tester_backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.apitester.api_tester_backend.dto.request.ApiExecuteRequest;
import com.apitester.api_tester_backend.dto.response.ApiExecuteResponse;
import com.apitester.api_tester_backend.entity.User;
import com.apitester.api_tester_backend.entity.enums.AuthType;
import com.apitester.api_tester_backend.entity.enums.HttpMethod;
import com.apitester.api_tester_backend.exception.ApiException;
import com.apitester.api_tester_backend.metrics.ApiExecutionMetrics;
import com.apitester.api_tester_backend.util.ApiRequestValidator;
import com.apitester.api_tester_backend.util.EnvironmentVariableResolver;
import com.apitester.api_tester_backend.util.SecurityUtil;
import com.apitester.api_tester_backend.util.SensitiveDataMasker;
import com.apitester.api_tester_backend.util.UrlValidator;

@ExtendWith(MockitoExtension.class)
class ApiExecutionServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private UrlValidator urlValidator;

    @Mock
    private EnvironmentVariableResolver environmentVariableResolver;

    @Mock
    private EnvironmentService environmentService;

    @Mock
    private ApiHistoryService apiHistoryService;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private SensitiveDataMasker sensitiveDataMasker;

    @Mock
    private SecurityUtil securityUtil;

    @Mock
    private RateLimitService rateLimitService;

    @Mock
    private ApiRequestValidator apiRequestValidator;

    @Mock
    private ApiExecutionMetrics apiExecutionMetrics;

    @InjectMocks
    private ApiExecutionService apiExecutionService;

    private User user;

    @BeforeEach
    void setUp() {

        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .build();

        when(securityUtil.getCurrentUser())
                .thenReturn(user);

        doNothing().when(rateLimitService)
                .checkRateLimit(anyString());

        doNothing().when(apiRequestValidator)
                .validate(any(ApiExecuteRequest.class));

        doNothing().when(urlValidator)
                .validate(anyString());

        doNothing().when(authenticationService)
                .applyAuthentication(
                        any(HttpHeaders.class),
                        any(),
                        any(),
                        any(),
                        any(),
                        any(),
                        any());

        when(environmentVariableResolver.resolve(
                any(),
                anyMap()))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    // =========================================================
    // 1. SUCCESSFUL GET REQUEST
    // =========================================================

    @Test
    void shouldExecuteGetRequestSuccessfully() {

        // Arrange

        ApiExecuteRequest request = ApiExecuteRequest.builder()
                .method(HttpMethod.GET)
                .url("https://example.com/users")
                .build();

        ResponseEntity<String> response = ResponseEntity
                .ok("{\"id\":1,\"name\":\"John\"}");

        when(restTemplate.exchange(
                eq("https://example.com/users"),
                eq(org.springframework.http.HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)))
                .thenReturn(response);

        // Act

        ApiExecuteResponse result = apiExecutionService.execute(request);

        // Assert

        assertNotNull(result);

        assertEquals(
                200,
                result.getStatusCode());

        assertEquals(
                "{\"id\":1,\"name\":\"John\"}",
                result.getBody());

        assertNotNull(
                result.getHeaders());

        assertNotNull(
                result.getResponseTime());

        assertEquals(
                "{\"id\":1,\"name\":\"John\"}"
                        .getBytes(StandardCharsets.UTF_8).length,
                result.getResponseSize());

        // Verify validation

        verify(apiRequestValidator)
                .validate(request);

        // Verify rate limiting

        verify(rateLimitService)
                .checkRateLimit("test@example.com");

        // Verify URL validation

        verify(urlValidator)
                .validate("https://example.com/users");

        // Verify RestTemplate

        verify(restTemplate)
                .exchange(
                        eq("https://example.com/users"),
                        eq(org.springframework.http.HttpMethod.GET),
                        any(HttpEntity.class),
                        eq(String.class));

        // Verify history

        verify(apiHistoryService)
                .saveHistory(
                        eq(request),
                        any(ApiExecuteResponse.class),
                        anyMap());

        // Verify metrics

        verify(apiExecutionMetrics)
                .incrementTotal();

        verify(apiExecutionMetrics)
                .incrementSuccess();
    }

    // =========================================================
    // 2. TARGET API RETURNS 404
    // =========================================================

    @Test
    void shouldReturnTargetApiErrorResponse() {

        // Arrange

        ApiExecuteRequest request = ApiExecuteRequest.builder()
                .method(HttpMethod.GET)
                .url("https://example.com/users/999")
                .build();

        String responseBody = "{\"error\":\"User not found\"}";

        HttpClientErrorException exception = HttpClientErrorException.create(
                HttpStatus.NOT_FOUND,
                "Not Found",
                HttpHeaders.EMPTY,
                responseBody.getBytes(StandardCharsets.UTF_8),
                StandardCharsets.UTF_8);

        when(restTemplate.exchange(
                eq("https://example.com/users/999"),
                eq(org.springframework.http.HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)))
                .thenThrow(exception);

        // Act

        ApiExecuteResponse result = apiExecutionService.execute(request);

        // Assert

        assertNotNull(result);

        assertEquals(
                404,
                result.getStatusCode());

        assertEquals(
                responseBody,
                result.getBody());

        assertNotNull(
                result.getHeaders());

        assertNotNull(
                result.getResponseTime());

        assertEquals(
                responseBody
                        .getBytes(StandardCharsets.UTF_8).length,
                result.getResponseSize());

        // Verify RestTemplate

        verify(restTemplate)
                .exchange(
                        eq("https://example.com/users/999"),
                        eq(org.springframework.http.HttpMethod.GET),
                        any(HttpEntity.class),
                        eq(String.class));

        // History should be saved even when target API
        // returns an HTTP error such as 404.

        verify(apiHistoryService)
                .saveHistory(
                        eq(request),
                        any(ApiExecuteResponse.class),
                        anyMap());

        // Verify metrics

        verify(apiExecutionMetrics)
                .incrementTotal();

        verify(apiExecutionMetrics)
                .incrementFailure();

        verify(apiExecutionMetrics, never())
                .incrementSuccess();
    }

    // =========================================================
    // 3. TARGET API IS UNREACHABLE
    // =========================================================

    @Test
    void shouldThrowGatewayTimeoutWhenApiIsUnreachable() {

        // Arrange

        ApiExecuteRequest request = ApiExecuteRequest.builder()
                .method(HttpMethod.GET)
                .url("https://unreachable-example.com")
                .build();

        when(restTemplate.exchange(
                eq("https://unreachable-example.com"),
                eq(org.springframework.http.HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)))
                .thenThrow(
                        new ResourceAccessException(
                                "Connection timeout"));

        // Act + Assert

        ApiException exception = assertThrows(
                ApiException.class,
                () -> apiExecutionService.execute(request));

        assertEquals(
                "Unable to connect to the target API",
                exception.getMessage());

        assertEquals(
                HttpStatus.GATEWAY_TIMEOUT,
                exception.getStatus());

        // Verify RestTemplate

        verify(restTemplate)
                .exchange(
                        eq("https://unreachable-example.com"),
                        eq(org.springframework.http.HttpMethod.GET),
                        any(HttpEntity.class),
                        eq(String.class));

        /*
         * Your current production code does NOT save history
         * when ResourceAccessException occurs.
         */

        verify(
                apiHistoryService,
                never())
                .saveHistory(
                        any(),
                        any(),
                        anyMap());

        // Verify metrics

        verify(apiExecutionMetrics)
                .incrementTotal();

        verify(apiExecutionMetrics)
                .incrementFailure();

        verify(apiExecutionMetrics, never())
                .incrementSuccess();
    }

    // =========================================================
    // 4. ENVIRONMENT VARIABLES ARE RESOLVED
    // =========================================================

    @Test
    void shouldResolveEnvironmentVariablesBeforeExecution() {

        // Arrange

        ApiExecuteRequest request = ApiExecuteRequest.builder()
                .method(HttpMethod.GET)
                .url("{{baseUrl}}/users")
                .environmentId(10L)
                .queryParams(
                        Map.of(
                                "status",
                                "{{status}}"))
                .build();

        Map<String, String> variables = Map.of(
                "baseUrl",
                "https://example.com",
                "status",
                "active");

        when(environmentService.getVariables(10L))
                .thenReturn(variables);

        /*
         * Resolve URL variable.
         */

        when(environmentVariableResolver.resolve(
                eq("{{baseUrl}}/users"),
                eq(variables)))
                .thenReturn("https://example.com/users");

        /*
         * Resolve query parameter variable.
         */

        when(environmentVariableResolver.resolve(
                eq("{{status}}"),
                eq(variables)))
                .thenReturn("active");

        ResponseEntity<String> response = ResponseEntity.ok(
                "{\"success\":true}");

        when(restTemplate.exchange(
                eq("https://example.com/users?status=active"),
                eq(org.springframework.http.HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)))
                .thenReturn(response);

        // Act

        ApiExecuteResponse result = apiExecutionService.execute(request);

        // Assert

        assertNotNull(result);

        assertEquals(
                200,
                result.getStatusCode());

        assertEquals(
                "{\"success\":true}",
                result.getBody());

        // Verify environment lookup

        verify(environmentService)
                .getVariables(10L);

        // Verify URL resolution

        verify(environmentVariableResolver)
                .resolve(
                        "{{baseUrl}}/users",
                        variables);

        // Verify query parameter resolution

        verify(environmentVariableResolver)
                .resolve(
                        "{{status}}",
                        variables);

        // Verify final URL

        verify(restTemplate)
                .exchange(
                        eq("https://example.com/users?status=active"),
                        eq(org.springframework.http.HttpMethod.GET),
                        any(HttpEntity.class),
                        eq(String.class));

        // Verify metrics

        verify(apiExecutionMetrics)
                .incrementTotal();

        verify(apiExecutionMetrics)
                .incrementSuccess();
    }

    // =========================================================
    // 5. BEARER AUTHENTICATION
    // =========================================================

    @Test
    void shouldApplyAuthenticationBeforeExecution() {

        // Arrange

        ApiExecuteRequest request = ApiExecuteRequest.builder()
                .method(HttpMethod.GET)
                .url("https://example.com/users")
                .authType(AuthType.BEARER_TOKEN)
                .bearerToken("my-secret-token")
                .build();

        ResponseEntity<String> response = ResponseEntity.ok(
                "{\"message\":\"success\"}");

        when(restTemplate.exchange(
                eq("https://example.com/users"),
                eq(org.springframework.http.HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)))
                .thenReturn(response);

        // Act

        ApiExecuteResponse result = apiExecutionService.execute(request);

        // Assert

        assertNotNull(result);

        assertEquals(
                200,
                result.getStatusCode());

        // Verify authentication

        verify(authenticationService)
                .applyAuthentication(
                        any(HttpHeaders.class),
                        eq(AuthType.BEARER_TOKEN),
                        eq("my-secret-token"),
                        eq(null),
                        eq(null),
                        eq(null),
                        eq(null));

        // Verify actual request

        verify(restTemplate)
                .exchange(
                        eq("https://example.com/users"),
                        eq(org.springframework.http.HttpMethod.GET),
                        any(HttpEntity.class),
                        eq(String.class));

        // Verify metrics

        verify(apiExecutionMetrics)
                .incrementTotal();

        verify(apiExecutionMetrics)
                .incrementSuccess();
    }

    // =========================================================
    // 6. POST REQUEST WITH JSON BODY
    // =========================================================

    @Test
    void shouldExecutePostRequestWithJsonBody() {

        // Arrange

        String requestBody =
                "{\"name\":\"John\",\"email\":\"john@example.com\"}";

        ApiExecuteRequest request = ApiExecuteRequest.builder()
                .method(HttpMethod.POST)
                .url("https://example.com/users")
                .body(requestBody)
                .build();

        ResponseEntity<String> response = ResponseEntity
                .status(HttpStatus.CREATED)
                .body("{\"id\":10}");

        when(restTemplate.exchange(
                eq("https://example.com/users"),
                eq(org.springframework.http.HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)))
                .thenReturn(response);

        // Act

        ApiExecuteResponse result = apiExecutionService.execute(request);

        // Assert

        assertNotNull(result);

        assertEquals(
                201,
                result.getStatusCode());

        assertEquals(
                "{\"id\":10}",
                result.getBody());

        // Capture HttpEntity

        ArgumentCaptor<HttpEntity<String>> entityCaptor =
                ArgumentCaptor.forClass(HttpEntity.class);

        verify(restTemplate)
                .exchange(
                        eq("https://example.com/users"),
                        eq(org.springframework.http.HttpMethod.POST),
                        entityCaptor.capture(),
                        eq(String.class));

        HttpEntity<String> entity = entityCaptor.getValue();

        assertNotNull(entity);

        assertEquals(
                requestBody,
                entity.getBody());

        assertNotNull(
                entity.getHeaders().getContentType());

        assertEquals(
                "application/json",
                entity.getHeaders()
                        .getContentType()
                        .toString());

        // Verify metrics

        verify(apiExecutionMetrics)
                .incrementTotal();

        verify(apiExecutionMetrics)
                .incrementSuccess();
    }

    // =========================================================
    // 7. REQUEST HEADERS ARE SENT
    // =========================================================

    @Test
    void shouldSendRequestHeaders() {

        // Arrange

        ApiExecuteRequest request = ApiExecuteRequest.builder()
                .method(HttpMethod.GET)
                .url("https://example.com/users")
                .headers(
                        Map.of(
                                "X-Custom-Header",
                                "hello",
                                "Accept",
                                "application/json"))
                .build();

        ResponseEntity<String> response = ResponseEntity.ok(
                "{\"success\":true}");

        when(restTemplate.exchange(
                eq("https://example.com/users"),
                eq(org.springframework.http.HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)))
                .thenReturn(response);

        // Act

        ApiExecuteResponse result = apiExecutionService.execute(request);

        // Assert

        assertNotNull(result);

        assertEquals(
                200,
                result.getStatusCode());

        // Capture HttpEntity

        ArgumentCaptor<HttpEntity<String>> entityCaptor =
                ArgumentCaptor.forClass(HttpEntity.class);

        verify(restTemplate)
                .exchange(
                        eq("https://example.com/users"),
                        eq(org.springframework.http.HttpMethod.GET),
                        entityCaptor.capture(),
                        eq(String.class));

        HttpHeaders headers = entityCaptor
                .getValue()
                .getHeaders();

        assertEquals(
                "hello",
                headers.getFirst("X-Custom-Header"));

        assertEquals(
                "application/json",
                headers.getFirst("Accept"));

        // Verify metrics

        verify(apiExecutionMetrics)
                .incrementTotal();

        verify(apiExecutionMetrics)
                .incrementSuccess();
    }

    // =========================================================
    // 8. QUERY PARAMETERS
    // =========================================================

    @Test
    void shouldAddQueryParametersToUrl() {

        // Arrange

        ApiExecuteRequest request = ApiExecuteRequest.builder()
                .method(HttpMethod.GET)
                .url("https://example.com/users")
                .queryParams(
                        Map.of(
                                "page",
                                "1",
                                "size",
                                "10"))
                .build();

        ResponseEntity<String> response = ResponseEntity.ok("[]");

        when(restTemplate.exchange(
                anyString(),
                eq(org.springframework.http.HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)))
                .thenReturn(response);

        // Act

        ApiExecuteResponse result = apiExecutionService.execute(request);

        // Assert

        assertNotNull(result);

        assertEquals(
                200,
                result.getStatusCode());

        // Capture final URL

        ArgumentCaptor<String> urlCaptor =
                ArgumentCaptor.forClass(String.class);

        verify(restTemplate)
                .exchange(
                        urlCaptor.capture(),
                        eq(org.springframework.http.HttpMethod.GET),
                        any(HttpEntity.class),
                        eq(String.class));

        String finalUrl = urlCaptor.getValue();

        assertEquals(
                true,
                finalUrl.contains("page=1"));

        assertEquals(
                true,
                finalUrl.contains("size=10"));

        // Verify metrics

        verify(apiExecutionMetrics)
                .incrementTotal();

        verify(apiExecutionMetrics)
                .incrementSuccess();
    }

    // =========================================================
    // 9. DELETE REQUEST
    // =========================================================

    @Test
    void shouldExecuteDeleteRequest() {

        // Arrange

        ApiExecuteRequest request = ApiExecuteRequest.builder()
                .method(HttpMethod.DELETE)
                .url("https://example.com/users/10")
                .build();

        ResponseEntity<String> response = ResponseEntity
                .noContent()
                .build();

        when(restTemplate.exchange(
                eq("https://example.com/users/10"),
                eq(org.springframework.http.HttpMethod.DELETE),
                any(HttpEntity.class),
                eq(String.class)))
                .thenReturn(response);

        // Act

        ApiExecuteResponse result = apiExecutionService.execute(request);

        // Assert

        assertNotNull(result);

        assertEquals(
                204,
                result.getStatusCode());

        assertEquals(
                0,
                result.getResponseSize());

        // Verify

        verify(restTemplate)
                .exchange(
                        eq("https://example.com/users/10"),
                        eq(org.springframework.http.HttpMethod.DELETE),
                        any(HttpEntity.class),
                        eq(String.class));

        // Verify metrics

        verify(apiExecutionMetrics)
                .incrementTotal();

        verify(apiExecutionMetrics)
                .incrementSuccess();
    }

    // =========================================================
    // 10. HISTORY SAVES MASKED HEADERS
    // =========================================================

    @Test
    void shouldSaveHistoryWithMaskedHeaders() {

        // Arrange

        ApiExecuteRequest request = ApiExecuteRequest.builder()
                .method(HttpMethod.GET)
                .url("https://example.com/users")
                .headers(
                        Map.of(
                                "Authorization",
                                "Bearer secret-token"))
                .build();

        ResponseEntity<String> response = ResponseEntity.ok(
                "{\"success\":true}");

        when(restTemplate.exchange(
                eq("https://example.com/users"),
                eq(org.springframework.http.HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)))
                .thenReturn(response);

        Map<String, String> maskedHeaders = Map.of(
                "Authorization",
                "********");

        when(sensitiveDataMasker.maskHeaders(anyMap()))
                .thenReturn(maskedHeaders);

        // Act

        ApiExecuteResponse result =
                apiExecutionService.execute(request);

        // Assert

        assertNotNull(result);

        assertEquals(
                200,
                result.getStatusCode());

        // Verify masking

        verify(sensitiveDataMasker)
                .maskHeaders(anyMap());

        // Verify history

        verify(apiHistoryService)
                .saveHistory(
                        eq(request),
                        any(ApiExecuteResponse.class),
                        eq(maskedHeaders));

        // Verify metrics

        verify(apiExecutionMetrics)
                .incrementTotal();

        verify(apiExecutionMetrics)
                .incrementSuccess();
    }
}