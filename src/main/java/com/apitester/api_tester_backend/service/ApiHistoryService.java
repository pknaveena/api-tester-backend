package com.apitester.api_tester_backend.service;

import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.apitester.api_tester_backend.dto.request.ApiExecuteRequest;
import com.apitester.api_tester_backend.dto.response.ApiExecuteResponse;
import com.apitester.api_tester_backend.dto.response.HistoryResponse;
import com.apitester.api_tester_backend.dto.response.HistoryResponseDetailed;
import com.apitester.api_tester_backend.entity.ApiHistory;
import com.apitester.api_tester_backend.entity.User;
import com.apitester.api_tester_backend.exception.ApiException;
import com.apitester.api_tester_backend.repository.ApiHistoryRepository;
import com.apitester.api_tester_backend.util.JsonUtil;
import com.apitester.api_tester_backend.util.SecurityUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApiHistoryService {
	private final ApiHistoryRepository apiHistoryRepository;
	private final JsonUtil jsonUtil;
	private final SecurityUtil securityUtil;

	public void saveHistory(ApiExecuteRequest request, ApiExecuteResponse response,
			Map<String, String> resolvedHeaders) {

		User user = securityUtil.getCurrentUser();

		try {

			ApiHistory history = ApiHistory.builder()
					.requestId(UUID.randomUUID().toString())
					.method(request.getMethod())
					// Store the Original URL Suppose the user entered: {{baseUrl}}/users
					.url(request.getUrl())
					.requestBody(request.getBody())
					.requestHeaders(jsonUtil.toJson(resolvedHeaders))
					.responseBody(response.getBody())
					.statusCode(response.getStatusCode())
					.responseTime(response.getResponseTime())
					.responseSize(response.getResponseSize())
					.user(user)
					.build();

			apiHistoryRepository.save(history);

		} catch (Exception ex) {

			throw new ApiException("Failed to save history", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public Page<HistoryResponse> getHistory(Pageable pageable) {

		User user = securityUtil.getCurrentUser();

		return apiHistoryRepository
				.findByUserOrderByIdDesc(user, pageable)
				.map(this::toResponse);
	}

	public HistoryResponseDetailed getHistoryById(Long id) {

		User user = securityUtil.getCurrentUser();

		ApiHistory history = apiHistoryRepository
				.findByIdAndUser(id, user)
				.orElseThrow(() -> new ApiException(
						"History not found",
						HttpStatus.NOT_FOUND));

		return toResponseDetailed(history);
	}

	public void deleteHistory(Long id) {

		User user = securityUtil.getCurrentUser();

		ApiHistory history = apiHistoryRepository
				.findByIdAndUser(id, user)
				.orElseThrow(() -> new ApiException(
						"History not found",
						HttpStatus.NOT_FOUND));

		apiHistoryRepository.delete(history);
	}

	private HistoryResponse toResponse(
			ApiHistory history) {

		return HistoryResponse.builder()
				.id(history.getId())
				.requestId(history.getRequestId())
				.method(history.getMethod())
				.url(history.getUrl())
				.statusCode(history.getStatusCode())
				.responseTime(history.getResponseTime())
				.responseSize(history.getResponseSize())
				.createdAt(history.getCreatedAt())
				.build();
	}

	private HistoryResponseDetailed toResponseDetailed(
			ApiHistory history) {

		return HistoryResponseDetailed.builder()
				.id(history.getId())
				.requestId(history.getRequestId())
				.method(history.getMethod())
				.url(history.getUrl())
				.requestBody(history.getRequestBody())
				.requestHeaders(history.getRequestHeaders())
				.responseBody(history.getResponseBody())
				.statusCode(history.getStatusCode())
				.responseTime(history.getResponseTime())
				.responseSize(history.getResponseSize())
				.createdAt(history.getCreatedAt())
				.build();
	}
}
