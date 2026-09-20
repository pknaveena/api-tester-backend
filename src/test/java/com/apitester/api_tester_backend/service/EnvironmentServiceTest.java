package com.apitester.api_tester_backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import com.apitester.api_tester_backend.dto.request.EnvironmentRequest;
import com.apitester.api_tester_backend.dto.response.EnvironmentResponse;
import com.apitester.api_tester_backend.entity.Environment;
import com.apitester.api_tester_backend.entity.User;
import com.apitester.api_tester_backend.exception.ApiException;
import com.apitester.api_tester_backend.repository.EnvironmentRepository;
import com.apitester.api_tester_backend.util.JsonUtil;
import com.apitester.api_tester_backend.util.SecurityUtil;

@ExtendWith(MockitoExtension.class)
public class EnvironmentServiceTest {

    @Mock
    private EnvironmentRepository environmentRepository;

    @Mock
    private JsonUtil jsonUtil;

    @Mock
    private SecurityUtil securityUtil;

    @InjectMocks
    private EnvironmentService environmentService;

    @Test
    void shouldCreateEnvironment() {

        // Arrange

        EnvironmentRequest request = new EnvironmentRequest();

        request.setName("Development");

        Map<String, String> variables = Map.of(
                "baseUrl",
                "https://dev.example.com",
                "token",
                "abc123");

        request.setVariables(variables);

        User user = new User();

        when(securityUtil.getCurrentUser())
                .thenReturn(user);

        String variablesJson = "{\"baseUrl\":\"https://dev.example.com\",\"token\":\"abc123\"}";

        when(jsonUtil.toJson(variables))
                .thenReturn(variablesJson);

        when(jsonUtil.fromJson(variablesJson))
                .thenReturn(variables);

        // Act

        EnvironmentResponse response = environmentService.create(request);

        // Assert

        assertNotNull(response);

        assertEquals(
                "Development",
                response.getName());

        assertEquals(
                variables,
                response.getVariables());

        verify(securityUtil)
                .getCurrentUser();

        verify(jsonUtil)
                .toJson(variables);

        verify(environmentRepository)
                .save(any(Environment.class));
    }

    @Test
    void shouldGetEnvironmentById() {

        // Arrange

        Long environmentId = 1L;

        User user = new User();

        Environment environment = Environment.builder()
                .id(environmentId)
                .name("Development")
                .variables(
                        "{\"baseUrl\":\"https://dev.example.com\"}")
                .user(user)
                .build();

        Map<String, String> variables = Map.of(
                "baseUrl",
                "https://dev.example.com");

        when(securityUtil.getCurrentUser())
                .thenReturn(user);

        when(environmentRepository.findByIdAndUser(
                environmentId,
                user)).thenReturn(
                        java.util.Optional.of(environment));

        when(jsonUtil.fromJson(
                environment.getVariables())).thenReturn(variables);

        // Act

        EnvironmentResponse response = environmentService.getById(environmentId);

        // Assert

        assertNotNull(response);

        assertEquals(
                environmentId,
                response.getId());

        assertEquals(
                "Development",
                response.getName());

        assertEquals(
                variables,
                response.getVariables());

        verify(environmentRepository)
                .findByIdAndUser(
                        environmentId,
                        user);
    }

    @Test
    void shouldThrowExceptionWhenEnvironmentNotFound() {

        // Arrange

        Long environmentId = 999L;

        User user = new User();

        when(securityUtil.getCurrentUser())
                .thenReturn(user);

        when(environmentRepository.findByIdAndUser(
                environmentId,
                user)).thenReturn(
                        java.util.Optional.empty());

        // Act + Assert

        ApiException exception = assertThrows(
                ApiException.class,
                () -> environmentService.getById(
                        environmentId));

        assertEquals(
                "Environment not found",
                exception.getMessage());

        assertEquals(
                HttpStatus.NOT_FOUND,
                exception.getStatus());
    }

    @Test
    void shouldDeleteEnvironment() {

        // Arrange

        Long environmentId = 1L;

        User user = new User();

        Environment environment = Environment.builder()
                .id(environmentId)
                .name("Development")
                .variables("{}")
                .user(user)
                .build();

        when(securityUtil.getCurrentUser())
                .thenReturn(user);

        when(environmentRepository.findByIdAndUser(
                environmentId,
                user)).thenReturn(
                        java.util.Optional.of(environment));

        // Act

        environmentService.delete(environmentId);

        // Assert

        verify(environmentRepository)
                .delete(environment);
    }

    @Test
    void shouldGetEnvironmentVariables() {

        // Arrange

        Long environmentId = 1L;

        User user = new User();

        Environment environment = Environment.builder()
                .id(environmentId)
                .name("Development")
                .variables(
                        "{\"baseUrl\":\"https://dev.example.com\"}")
                .user(user)
                .build();

        Map<String, String> expectedVariables = Map.of(
                "baseUrl",
                "https://dev.example.com");

        when(securityUtil.getCurrentUser())
                .thenReturn(user);

        when(environmentRepository.findByIdAndUser(
                environmentId,
                user)).thenReturn(
                        java.util.Optional.of(environment));

        when(jsonUtil.fromJson(
                environment.getVariables())).thenReturn(expectedVariables);

        // Act

        Map<String, String> result = environmentService.getVariables(
                environmentId);

        // Assert

        assertEquals(
                expectedVariables,
                result);
    }

    @Test
    void shouldUpdateEnvironment() {

        // Arrange

        Long environmentId = 1L;

        User user = new User();

        Environment environment = Environment.builder()
                .id(environmentId)
                .name("Old Name")
                .variables("{}")
                .user(user)
                .build();

        Map<String, String> variables = Map.of(
                "baseUrl",
                "https://test.example.com");

        EnvironmentRequest request = new EnvironmentRequest();

        request.setName("Testing");

        request.setVariables(variables);

        String variablesJson = "{\"baseUrl\":\"https://test.example.com\"}";

        when(securityUtil.getCurrentUser())
                .thenReturn(user);

        when(environmentRepository.findByIdAndUser(
                environmentId,
                user)).thenReturn(
                        java.util.Optional.of(environment));

        when(jsonUtil.toJson(variables))
                .thenReturn(variablesJson);

        when(jsonUtil.fromJson(variablesJson))
                .thenReturn(variables);

        // Act

        EnvironmentResponse response = environmentService.update(
                environmentId,
                request);

        // Assert

        assertEquals(
                "Testing",
                response.getName());

        assertEquals(
                variables,
                response.getVariables());

        verify(environmentRepository)
                .save(environment);
    }

    @Test
    void shouldGetAllEnvironments() {

        // Arrange

        User user = new User();

        Pageable pageable = PageRequest.of(0, 10);

        Environment environment = Environment.builder()
                .id(1L)
                .name("Development")
                .variables(
                        "{\"baseUrl\":\"https://dev.example.com\"}")
                .user(user)
                .build();

        Page<Environment> environmentPage = new PageImpl<>(
                java.util.List.of(environment));

        Map<String, String> variables = Map.of(
                "baseUrl",
                "https://dev.example.com");

        when(securityUtil.getCurrentUser())
                .thenReturn(user);

        when(environmentRepository.findByUser(
                user,
                pageable)).thenReturn(environmentPage);

        when(jsonUtil.fromJson(
                environment.getVariables())).thenReturn(variables);

        // Act

        Page<EnvironmentResponse> result = environmentService.getAll(pageable);

        // Assert

        assertNotNull(result);

        assertEquals(
                1,
                result.getTotalElements());

        assertEquals(
                "Development",
                result.getContent()
                        .get(0)
                        .getName());

        verify(environmentRepository)
                .findByUser(
                        user,
                        pageable);
    }
}
