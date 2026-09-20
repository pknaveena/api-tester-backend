package com.apitester.api_tester_backend.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.apitester.api_tester_backend.dto.request.ApiExecuteRequest;
import com.apitester.api_tester_backend.dto.request.CollectionItemExecuteRequest;
import com.apitester.api_tester_backend.dto.request.CollectionItemRequest;
import com.apitester.api_tester_backend.dto.response.ApiExecuteResponse;
import com.apitester.api_tester_backend.dto.response.CollectionItemResponse;
import com.apitester.api_tester_backend.entity.Collection;
import com.apitester.api_tester_backend.entity.CollectionItem;
import com.apitester.api_tester_backend.entity.Environment;
import com.apitester.api_tester_backend.entity.User;
import com.apitester.api_tester_backend.exception.ApiException;
import com.apitester.api_tester_backend.repository.CollectionItemRepository;
import com.apitester.api_tester_backend.repository.CollectionRepository;
import com.apitester.api_tester_backend.util.JsonUtil;
import com.apitester.api_tester_backend.util.SecurityUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CollectionItemService {

	private final CollectionRepository collectionRepository;
	private final CollectionItemRepository collectionItemRepository;
	private final JsonUtil jsonUtil;
	private final SecurityUtil securityUtil;
	private final ApiExecutionService apiExecutionService;
	private final EnvironmentService environmentService;

	public CollectionItemResponse create(Long collectionId,
			CollectionItemRequest request) {

		Collection collection = findCollection(collectionId);

		CollectionItem item = CollectionItem.builder()
				.name(request.getName())
				.method(request.getMethod())
				.url(request.getUrl())
				.requestBody(request.getBody())
				.requestHeaders(
						jsonUtil.toJson(request.getHeaders()))
				.queryParams(
						jsonUtil.toJson(request.getQueryParams()))
				.authType(request.getAuthType())
				.authData(jsonUtil.toJson(buildAuthData(request)))
				.collection(collection)
				.build();

		return toResponse(
				collectionItemRepository.save(item));
	}

	public List<CollectionItemResponse> getAll(
			Long collectionId) {

		Collection collection = findCollection(collectionId);

		return collectionItemRepository
				.findByCollection(collection)
				.stream()
				.map(this::toResponse)
				.toList();
	}

	public CollectionItemResponse getById(
			Long collectionId,
			Long itemId) {

		CollectionItem item = findCollectionItem(collectionId, itemId);

		return toResponse(item);
	}

	public CollectionItemResponse update(
			Long collectionId,
			Long itemId,
			CollectionItemRequest request) {

		CollectionItem item = findCollectionItem(collectionId, itemId);

		item.setName(request.getName());
		item.setMethod(request.getMethod());
		item.setUrl(request.getUrl());
		item.setRequestBody(
				request.getBody());
		item.setRequestHeaders(
				jsonUtil.toJson(
						request.getHeaders()));
		item.setQueryParams(
				jsonUtil.toJson(
						request.getQueryParams()));
		item.setAuthType(request.getAuthType());

		item.setAuthData(
				jsonUtil.toJson(buildAuthData(request)));
		return toResponse(
				collectionItemRepository.save(item));
	}

	public void delete(
			Long collectionId,
			Long itemId) {

		CollectionItem item = findCollectionItem(collectionId, itemId);

		collectionItemRepository.delete(item);
	}

	private Collection findCollection(
			Long collectionId) {

		User user = securityUtil.getCurrentUser();
		return collectionRepository
				.findByIdAndUser(
						collectionId,
						user)
				.orElseThrow(() -> new ApiException(
						"Collection not found",
						HttpStatus.NOT_FOUND));
	}

	private CollectionItem findCollectionItem(
			Long collectionId, Long collectionItemId) {

		Collection collection = findCollection(collectionId);

		return collectionItemRepository
				.findByIdAndCollection(
						collectionItemId,
						collection)
				.orElseThrow(() -> new ApiException(
						"Collection item not found",
						HttpStatus.NOT_FOUND));
	}

	private CollectionItemResponse toResponse(
			CollectionItem item) {

		Map<String, String> authData = jsonUtil.fromJson(item.getAuthData());
		return CollectionItemResponse.builder()
				.id(item.getId())
				.name(item.getName())
				.method(item.getMethod())
				.url(item.getUrl())
				.headers(
						jsonUtil.fromJson(
								item.getRequestHeaders()))
				.queryParams(
						jsonUtil.fromJson(
								item.getQueryParams()))
				.body(item.getRequestBody())
				.authType(item.getAuthType())
				.bearerToken(authData.get("bearerToken"))
				.username(authData.get("username"))
				.password(authData.get("password"))
				.apiKey(authData.get("apiKey"))
				.apiKeyName(authData.get("apiKeyName"))
				.build();
	}

	public ApiExecuteResponse execute(Long collectionId, Long itemId,
			CollectionItemExecuteRequest collectionItemRequest) {

		CollectionItem item = findCollectionItem(collectionId, itemId);

		Map<String, String> authData = jsonUtil.fromJson(item.getAuthData());
		Environment environment = environmentService
				.getOwnedEnvironment(collectionItemRequest.getEnvironmentId());
		ApiExecuteRequest request = ApiExecuteRequest.builder()
				.method(item.getMethod())
				.url(item.getUrl())
				.headers(jsonUtil.fromJson(item.getRequestHeaders()))
				.queryParams(jsonUtil.fromJson(item.getQueryParams()))
				.body(item.getRequestBody())
				.environmentId(environment.getId())
				// Authentication
				.authType(item.getAuthType())
				.bearerToken(
						authData.get("bearerToken"))
				.username(
						authData.get("username"))
				.password(
						authData.get("password"))
				.apiKey(
						authData.get("apiKey"))
				.apiKeyName(
						authData.get("apiKeyName"))

				.build();

		return apiExecutionService.execute(request);
	}

	private Map<String, String> buildAuthData(
			CollectionItemRequest request) {

		Map<String, String> authData = new HashMap<>();

		if (request.getBearerToken() != null) {
			authData.put(
					"bearerToken",
					request.getBearerToken());
		}

		if (request.getUsername() != null) {
			authData.put(
					"username",
					request.getUsername());
		}

		if (request.getPassword() != null) {
			authData.put(
					"password",
					request.getPassword());
		}

		if (request.getApiKey() != null) {
			authData.put(
					"apiKey",
					request.getApiKey());
		}

		if (request.getApiKeyName() != null) {
			authData.put(
					"apiKeyName",
					request.getApiKeyName());
		}

		return authData;
	}
}