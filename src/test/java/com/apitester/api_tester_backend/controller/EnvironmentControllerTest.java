package com.apitester.api_tester_backend.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.apitester.api_tester_backend.dto.request.EnvironmentRequest;
import com.apitester.api_tester_backend.dto.response.EnvironmentResponse;
import com.apitester.api_tester_backend.security.CustomUserDetailsService;
import com.apitester.api_tester_backend.security.JwtService;
import com.apitester.api_tester_backend.service.EnvironmentService;
import tools.jackson.databind.ObjectMapper;
@WebMvcTest(EnvironmentController.class)
@AutoConfigureMockMvc(addFilters = false)
class EnvironmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;


    @MockitoBean
    private EnvironmentService environmentService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Test
    void shouldCreateEnvironment() throws Exception {

        EnvironmentRequest request = EnvironmentRequest.builder()
                .name("Development")
                .variables(Map.of(
                        "baseUrl", "https://api.example.com"))
                .build();

        EnvironmentResponse response = EnvironmentResponse.builder()
                .id(1L)
                .name("Development")
                .variables(Map.of(
                        "baseUrl", "https://api.example.com"))
                .build();

        when(environmentService.create(any(EnvironmentRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                post("/api/environments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Development"))
                .andExpect(jsonPath("$.variables.baseUrl")
                        .value("https://api.example.com"));

        verify(environmentService).create(any(EnvironmentRequest.class));
    }

    @Test
    void shouldGetAllEnvironments() throws Exception {

        EnvironmentResponse response = EnvironmentResponse.builder()
                .id(1L)
                .name("Development")
                .variables(Map.of(
                        "baseUrl", "https://api.example.com"))
                .build();

        PageImpl<EnvironmentResponse> page =
                new PageImpl<>(
                        java.util.List.of(response),
                        PageRequest.of(0, 20),
                        1);

        when(environmentService.getAll(any(PageRequest.class)))
                .thenReturn(page);

        mockMvc.perform(
                get("/api/environments")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name")
                        .value("Development"));

        verify(environmentService)
                .getAll(any(PageRequest.class));
    }

    @Test
    void shouldGetEnvironmentById() throws Exception {

        EnvironmentResponse response = EnvironmentResponse.builder()
                .id(1L)
                .name("Development")
                .variables(Map.of(
                        "baseUrl", "https://api.example.com"))
                .build();

        when(environmentService.getById(1L))
                .thenReturn(response);

        mockMvc.perform(
                get("/api/environments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name")
                        .value("Development"));

        verify(environmentService).getById(1L);
    }

    @Test
    void shouldUpdateEnvironment() throws Exception {

        EnvironmentRequest request = EnvironmentRequest.builder()
                .name("Production")
                .variables(Map.of(
                        "baseUrl", "https://api.example.com"))
                .build();

        EnvironmentResponse response = EnvironmentResponse.builder()
                .id(1L)
                .name("Production")
                .variables(Map.of(
                        "baseUrl", "https://api.example.com"))
                .build();

        when(environmentService.update(
                eq(1L),
                any(EnvironmentRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                put("/api/environments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name")
                        .value("Production"));

        verify(environmentService).update(
                eq(1L),
                any(EnvironmentRequest.class));
    }

    @Test
    void shouldDeleteEnvironment() throws Exception {

        doNothing()
                .when(environmentService)
                .delete(1L);

        mockMvc.perform(
                delete("/api/environments/1"))
                .andExpect(status().isNoContent());

        verify(environmentService).delete(1L);
    }
}