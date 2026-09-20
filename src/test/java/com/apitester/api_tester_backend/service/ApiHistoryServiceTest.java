package com.apitester.api_tester_backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import com.apitester.api_tester_backend.dto.request.ApiExecuteRequest;
import com.apitester.api_tester_backend.dto.response.ApiExecuteResponse;
import com.apitester.api_tester_backend.dto.response.HistoryResponse;
import com.apitester.api_tester_backend.entity.ApiHistory;
import com.apitester.api_tester_backend.entity.User;
import com.apitester.api_tester_backend.exception.ApiException;
import com.apitester.api_tester_backend.repository.ApiHistoryRepository;
import com.apitester.api_tester_backend.util.JsonUtil;
import com.apitester.api_tester_backend.util.SecurityUtil;

@ExtendWith(MockitoExtension.class)
class ApiHistoryServiceTest {

    @Mock
    private ApiHistoryRepository apiHistoryRepository;

    @Mock
    private JsonUtil jsonUtil;

    @Mock
    private SecurityUtil securityUtil;

    @InjectMocks
    private ApiHistoryService apiHistoryService;

    private User user;

    @BeforeEach
    void setUp() {

        user = User.builder()
                .id(1L)
                .build();
    }

    // ==================================================
    // SAVE HISTORY
    // ==================================================

    @Test
    void shouldSaveHistory() {

        // Arrange

        ApiExecuteRequest request = ApiExecuteRequest.builder()
                .url("https://example.com/users")
                .body("{\"name\":\"John\"}")
                .build();

        ApiExecuteResponse response = ApiExecuteResponse.builder()
                .statusCode(200)
                .body("{\"id\":1}")
                .responseTime(150L)
                .responseSize(10L)
                .build();

        Map<String, String> headers = Map.of(
                "Content-Type",
                "application/json");

        when(securityUtil.getCurrentUser())
                .thenReturn(user);

        when(jsonUtil.toJson(headers))
                .thenReturn("{\"Content-Type\":\"application/json\"}");

        // Act

        apiHistoryService.saveHistory(
                request,
                response,
                headers);

        // Assert / Verify

        ArgumentCaptor<ApiHistory> captor =
                ArgumentCaptor.forClass(ApiHistory.class);

        verify(apiHistoryRepository)
                .save(captor.capture());

        ApiHistory savedHistory = captor.getValue();

        assertNotNull(savedHistory);
        assertNotNull(savedHistory.getRequestId());

        assertEquals(
                request.getMethod(),
                savedHistory.getMethod());

        assertEquals(
                "https://example.com/users",
                savedHistory.getUrl());

        assertEquals(
                "{\"name\":\"John\"}",
                savedHistory.getRequestBody());

        assertEquals(
                "{\"Content-Type\":\"application/json\"}",
                savedHistory.getRequestHeaders());

        assertEquals(
                "{\"id\":1}",
                savedHistory.getResponseBody());

        assertEquals(
                200,
                savedHistory.getStatusCode());

        assertEquals(
                150L,
                savedHistory.getResponseTime());

        assertEquals(
                10L,
                savedHistory.getResponseSize());

        assertEquals(
                user,
                savedHistory.getUser());

        verify(securityUtil)
                .getCurrentUser();

        verify(jsonUtil)
                .toJson(headers);
    }

    // ==================================================
    // SAVE HISTORY - FAILURE
    // ==================================================

    @Test
    void shouldThrowExceptionWhenSavingHistoryFails() {

        // Arrange

        ApiExecuteRequest request = ApiExecuteRequest.builder()
                .url("https://example.com/users")
                .build();

        ApiExecuteResponse response = ApiExecuteResponse.builder()
                .statusCode(200)
                .body("{}")
                .responseTime(100L)
                .responseSize(2L)
                .build();

        Map<String, String> headers = Map.of();

        when(securityUtil.getCurrentUser())
                .thenReturn(user);

        when(jsonUtil.toJson(headers))
                .thenThrow(new RuntimeException("Database error"));

        // Act

        ApiException exception = assertThrows(
                ApiException.class,
                () -> apiHistoryService.saveHistory(
                        request,
                        response,
                        headers));

        // Assert

        assertEquals(
                "Failed to save history",
                exception.getMessage());

        assertEquals(
                HttpStatus.INTERNAL_SERVER_ERROR,
                exception.getStatus());

        verify(apiHistoryRepository, never())
                .save(any(ApiHistory.class));
    }

    // ==================================================
    // GET HISTORY
    // ==================================================

    @Test
    void shouldGetHistory() {

        // Arrange

        Pageable pageable =
                PageRequest.of(0, 10);

        ApiHistory history1 = ApiHistory.builder()
                .id(1L)
                .requestId("request-1")
                .url("https://example.com/users")
                .statusCode(200)
                .responseTime(100L)
                .responseSize(20L)
                .user(user)
                .build();

        ApiHistory history2 = ApiHistory.builder()
                .id(2L)
                .requestId("request-2")
                .url("https://example.com/orders")
                .statusCode(201)
                .responseTime(200L)
                .responseSize(30L)
                .user(user)
                .build();

        Page<ApiHistory> historyPage =
                new PageImpl<>(
                        List.of(history1, history2),
                        pageable,
                        2);

        when(securityUtil.getCurrentUser())
                .thenReturn(user);

        when(apiHistoryRepository.findByUser(
                user,
                pageable))
                .thenReturn(historyPage);

        // Act

        Page<HistoryResponse> response =
                apiHistoryService.getHistory(pageable);

        // Assert

        assertNotNull(response);

        assertEquals(
                2,
                response.getContent().size());

        assertEquals(
                1L,
                response.getContent()
                        .get(0)
                        .getId());

        assertEquals(
                "request-1",
                response.getContent()
                        .get(0)
                        .getRequestId());

        assertEquals(
                "https://example.com/users",
                response.getContent()
                        .get(0)
                        .getUrl());

        assertEquals(
                200,
                response.getContent()
                        .get(0)
                        .getStatusCode());

        assertEquals(
                2L,
                response.getContent()
                        .get(1)
                        .getId());

        verify(securityUtil)
                .getCurrentUser();

        verify(apiHistoryRepository)
                .findByUser(
                        user,
                        pageable);
    }

    // ==================================================
    // GET HISTORY - EMPTY
    // ==================================================

    @Test
    void shouldReturnEmptyHistory() {

        // Arrange

        Pageable pageable =
                PageRequest.of(0, 10);

        Page<ApiHistory> emptyPage =
                new PageImpl<>(
                        List.of(),
                        pageable,
                        0);

        when(securityUtil.getCurrentUser())
                .thenReturn(user);

        when(apiHistoryRepository.findByUser(
                user,
                pageable))
                .thenReturn(emptyPage);

        // Act

        Page<HistoryResponse> response =
                apiHistoryService.getHistory(pageable);

        // Assert

        assertNotNull(response);

        assertEquals(
                0,
                response.getContent().size());

        verify(apiHistoryRepository)
                .findByUser(
                        user,
                        pageable);
    }

    // ==================================================
    // GET HISTORY BY ID
    // ==================================================

    @Test
    void shouldGetHistoryById() {

        // Arrange

        ApiHistory history = ApiHistory.builder()
                .id(10L)
                .requestId("request-10")
                .url("https://example.com/users")
                .statusCode(200)
                .responseTime(150L)
                .responseSize(25L)
                .user(user)
                .build();

        when(securityUtil.getCurrentUser())
                .thenReturn(user);

        when(apiHistoryRepository.findByIdAndUser(
                10L,
                user))
                .thenReturn(Optional.of(history));

        // Act

        HistoryResponse response =
                apiHistoryService.getHistoryById(10L);

        // Assert

        assertNotNull(response);

        assertEquals(
                10L,
                response.getId());

        assertEquals(
                "request-10",
                response.getRequestId());

        assertEquals(
                "https://example.com/users",
                response.getUrl());

        assertEquals(
                200,
                response.getStatusCode());

        assertEquals(
                150L,
                response.getResponseTime());

        assertEquals(
                25L,
                response.getResponseSize());

        verify(apiHistoryRepository)
                .findByIdAndUser(
                        10L,
                        user);
    }

    // ==================================================
    // GET HISTORY BY ID - NOT FOUND
    // ==================================================

    @Test
    void shouldThrowExceptionWhenHistoryNotFound() {

        // Arrange

        when(securityUtil.getCurrentUser())
                .thenReturn(user);

        when(apiHistoryRepository.findByIdAndUser(
                99L,
                user))
                .thenReturn(Optional.empty());

        // Act

        ApiException exception = assertThrows(
                ApiException.class,
                () -> apiHistoryService
                        .getHistoryById(99L));

        // Assert

        assertEquals(
                "History not found",
                exception.getMessage());

        assertEquals(
                HttpStatus.NOT_FOUND,
                exception.getStatus());
    }

    // ==================================================
    // DELETE HISTORY
    // ==================================================

    @Test
    void shouldDeleteHistory() {

        // Arrange

        ApiHistory history = ApiHistory.builder()
                .id(10L)
                .requestId("request-10")
                .url("https://example.com/users")
                .user(user)
                .build();

        when(securityUtil.getCurrentUser())
                .thenReturn(user);

        when(apiHistoryRepository.findByIdAndUser(
                10L,
                user))
                .thenReturn(Optional.of(history));

        // Act

        apiHistoryService.deleteHistory(10L);

        // Assert / Verify

        verify(apiHistoryRepository)
                .findByIdAndUser(
                        10L,
                        user);

        verify(apiHistoryRepository)
                .delete(history);
    }

    // ==================================================
    // DELETE HISTORY - NOT FOUND
    // ==================================================

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingHistory() {

        // Arrange

        when(securityUtil.getCurrentUser())
                .thenReturn(user);

        when(apiHistoryRepository.findByIdAndUser(
                99L,
                user))
                .thenReturn(Optional.empty());

        // Act

        ApiException exception = assertThrows(
                ApiException.class,
                () -> apiHistoryService
                        .deleteHistory(99L));

        // Assert

        assertEquals(
                "History not found",
                exception.getMessage());

        assertEquals(
                HttpStatus.NOT_FOUND,
                exception.getStatus());

        verify(apiHistoryRepository, never())
                .delete(any(ApiHistory.class));
    }
}