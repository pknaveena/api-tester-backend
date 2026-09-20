package com.apitester.api_tester_backend.service;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.apitester.api_tester_backend.dto.request.EnvironmentRequest;
import com.apitester.api_tester_backend.dto.response.EnvironmentResponse;
import com.apitester.api_tester_backend.entity.Environment;
import com.apitester.api_tester_backend.entity.User;
import com.apitester.api_tester_backend.exception.ApiException;
import com.apitester.api_tester_backend.repository.EnvironmentRepository;
import com.apitester.api_tester_backend.util.JsonUtil;
import com.apitester.api_tester_backend.util.SecurityUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EnvironmentService {
    private final EnvironmentRepository environmentRepository;
    private final JsonUtil jsonUtil;
    private final SecurityUtil securityUtil;

    public EnvironmentResponse create(EnvironmentRequest request) {

        User user = securityUtil.getCurrentUser();

        Environment environment = Environment.builder()
                .name(request.getName())
                .variables(jsonUtil.toJson(request.getVariables()))
                .user(user)
                .build();

        environmentRepository.save(environment);

        return mapToResponse(environment);
    }

    public EnvironmentResponse update(Long id, EnvironmentRequest request) {

        Environment environment = getOwnedEnvironment(id);

        environment.setName(request.getName());
        environment.setVariables(jsonUtil.toJson(request.getVariables()));
        environmentRepository.save(environment);

        return mapToResponse(environment);
    }

    public Page<EnvironmentResponse> getAll(Pageable pageable) {

        User user = securityUtil.getCurrentUser();

        return environmentRepository
                .findByUser(user, pageable)
                .map(this::mapToResponse);
    }

    public EnvironmentResponse getById(Long id) {
        Environment environment = getOwnedEnvironment(id);

        return mapToResponse(environment);
    }

    public void delete(Long id) {
        Environment environment = getOwnedEnvironment(id);

        environmentRepository.delete(environment);
    }

    public Map<String, String> getVariables(Long id) {

        Environment environment = getOwnedEnvironment(id);                   

        return jsonUtil.fromJson(environment.getVariables());
    }

    public Environment getOwnedEnvironment(Long id) {
        User user = securityUtil.getCurrentUser();

        Environment environment = environmentRepository
                .findByIdAndUser(id, user)
                .orElseThrow(() -> new ApiException(
                        "Environment not found",
                        HttpStatus.NOT_FOUND));

        return environment;
    }

    private EnvironmentResponse mapToResponse(Environment environment) {

        return EnvironmentResponse.builder()
                .id(environment.getId())
                .name(environment.getName())
                .variables(jsonUtil.fromJson(environment.getVariables()))
                .build();

    }
}
