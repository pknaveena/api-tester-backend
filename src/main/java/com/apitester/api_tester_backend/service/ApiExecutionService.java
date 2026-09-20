package com.apitester.api_tester_backend.service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.apitester.api_tester_backend.dto.request.ApiExecuteRequest;
import com.apitester.api_tester_backend.dto.response.ApiExecuteResponse;
import com.apitester.api_tester_backend.exception.ApiException;
import com.apitester.api_tester_backend.metrics.ApiExecutionMetrics;
import com.apitester.api_tester_backend.util.ApiRequestValidator;
import com.apitester.api_tester_backend.util.EnvironmentVariableResolver;
import com.apitester.api_tester_backend.util.SecurityUtil;
import com.apitester.api_tester_backend.util.SensitiveDataMasker;
import com.apitester.api_tester_backend.util.UrlValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApiExecutionService {

	private final RestTemplate restTemplate;

	private final UrlValidator urlValidator;

	private final EnvironmentVariableResolver environmentVariableResolver;

	private final EnvironmentService environmentService;

	private final ApiHistoryService apiHistoryService;

	private final AuthenticationService authenticationService;

	private final SensitiveDataMasker sensitiveDataMasker;

	private final SecurityUtil securityUtil;

	private final RateLimitService rateLimitService;

	private final ApiRequestValidator apiRequestValidator;

	private final ApiExecutionMetrics apiExecutionMetrics;

	public ApiExecuteResponse execute(ApiExecuteRequest request) {
		// a sample request
		// URL:
		// {{baseUrl}}/users/{{userId}}

		// Headers:
		// Authorization: Bearer {{token}}

		// Query Parameters:
		// api_key = {{apiKey}}

		// Body:
		// {
		// "username": "{{username}}"
		// }

		// check ratelimiting
		String userKey = securityUtil.getCurrentUser().getEmail();
		rateLimitService.checkRateLimit(userKey);

		apiExecutionMetrics.incrementTotal();

		// validate request
		apiRequestValidator.validate(request);

		String url = request.getUrl();

		// Load environment variables.
		Map<String, String> variables = request.getEnvironmentId() != null
				? environmentService.getVariables(
						request.getEnvironmentId())
				: Map.of();

		// Resolve variables in URL.
		url = environmentVariableResolver.resolve(
				url,
				variables);

		// Build URL with query parameters.
		UriComponentsBuilder uriBuilder = UriComponentsBuilder
				.fromUriString(url);

		if (request.getQueryParams() != null) {

			request.getQueryParams()
					.forEach((key, value) -> {

						String resolvedValue = environmentVariableResolver.resolve(
								value,
								variables);

						uriBuilder.queryParam(
								key,
								resolvedValue);
					});
		}

		String finalUrl = uriBuilder
				.build()
				.encode()
				.toUriString();

		try {
			// Validate the final resolved URL.
			urlValidator.validate(finalUrl);

			// Resolve variables in headers.
			HttpHeaders headers = new HttpHeaders();

			if (request.getHeaders() != null) {

				request.getHeaders()
						.forEach((key, value) -> {

							String resolvedValue = environmentVariableResolver.resolve(
									value,
									variables);

							headers.set(
									key,
									resolvedValue);
						});
			}

			// Resolve variables in request body.
			String body = request.getBody();

			if (body != null) {

				// content-type handling.
				if (!body.isBlank() && headers.getContentType() == null) {
					headers.setContentType(MediaType.APPLICATION_JSON);
				}

				body = environmentVariableResolver.resolve(
						body,
						variables);
			}

			// resolve authentication fields
			String bearerToken = environmentVariableResolver.resolve(
					request.getBearerToken(),
					variables);

			String username = environmentVariableResolver.resolve(
					request.getUsername(),
					variables);

			String password = environmentVariableResolver.resolve(
					request.getPassword(),
					variables);

			String apiKey = environmentVariableResolver.resolve(
					request.getApiKey(),
					variables);

			String apiKeyName = environmentVariableResolver.resolve(
					request.getApiKeyName(),
					variables);

			authenticationService.applyAuthentication(
					headers,
					request.getAuthType(),
					bearerToken,
					username,
					password,
					apiKeyName,
					apiKey);

			// HttpEntity = Headers + Body
			HttpEntity<String> entity = new HttpEntity<>(
					body,
					headers);

			HttpMethod httpMethod = HttpMethod.valueOf(
					request.getMethod().name());

			long startTime = System.currentTimeMillis();

			try {
				ResponseEntity<String> response = restTemplate.exchange(
						finalUrl,
						httpMethod,
						entity,
						String.class);
				ApiExecuteResponse result = buildResponse(
						response,
						startTime);

				if (response.getStatusCode().is2xxSuccessful()) {
					apiExecutionMetrics.incrementSuccess();
				}
				saveHistory(request, result, headers);

				return result;

			} catch (HttpStatusCodeException e) {

				log.error("HTTP STATUS EXCEPTION: {}", e.getStatusCode());

				apiExecutionMetrics.incrementFailure();

				ApiExecuteResponse result = buildErrorResponse(e, startTime);
				saveHistory(request, result, headers);
				return result;

			} catch (ResourceAccessException e) {

				log.error("RESOURCE ACCESS EXCEPTION", e);

				throw new ApiException(
						"Unable to connect to the target API",
						HttpStatus.GATEWAY_TIMEOUT);
			}

		} catch (ApiException e) {

			apiExecutionMetrics.incrementFailure();

			throw e;
		}
	}

	private ApiExecuteResponse buildResponse(ResponseEntity<String> response, long startTime) {

		long responseTime = System.currentTimeMillis() - startTime;

		String responseBody = response.getBody();

		long responseSize = responseBody == null
				? 0
				: responseBody.getBytes(
						StandardCharsets.UTF_8).length;
		Map<String, List<String>> responseHeaders = response.getHeaders()
				.headerNames()
				.stream()
				.collect(
						Collectors.toMap(
								name -> name,
								name -> response.getHeaders().get(name)));

		return ApiExecuteResponse.builder()
				.statusCode(response.getStatusCode().value())
				.statusText(response.getStatusCode().toString())
				.headers(responseHeaders)
				.body(responseBody)
				.responseTime(responseTime)
				.responseSize(responseSize)
				.build();
	}

	private ApiExecuteResponse buildErrorResponse(
			HttpStatusCodeException exception,
			long startTime) {

		long responseTime = System.currentTimeMillis() - startTime;

		String responseBody = exception.getResponseBodyAsString();

		long responseSize = responseBody == null
				? 0
				: responseBody.getBytes(
						StandardCharsets.UTF_8).length;

		Map<String, List<String>> responseHeaders = exception.getResponseHeaders() == null
				? Map.of()
				: exception.getResponseHeaders()
						.headerNames()
						.stream()
						.collect(
								Collectors.toMap(
										name -> name,
										name -> exception.getResponseHeaders().get(name)));

		return ApiExecuteResponse.builder()
				.statusCode(exception.getStatusCode().value())
				.statusText(exception.getStatusCode().toString())
				.headers(responseHeaders)
				.body(responseBody)
				.responseTime(responseTime)
				.responseSize(responseSize)
				.build();
	}

	private void saveHistory(
			ApiExecuteRequest request,
			ApiExecuteResponse response,
			HttpHeaders headers) {

		Map<String, String> resolvedHeaders = headers.headerNames()
				.stream()
				.collect(Collectors.toMap(
						name -> name,
						name -> String.join(",", headers.get(name))));

		Map<String, String> maskedHeaders = sensitiveDataMasker.maskHeaders(
				resolvedHeaders);
		apiHistoryService.saveHistory(
				request,
				response,
				maskedHeaders);
	}
}