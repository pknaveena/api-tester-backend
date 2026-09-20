package com.apitester.api_tester_backend.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.apitester.api_tester_backend.dto.request.EnvironmentRequest;
import com.apitester.api_tester_backend.dto.request.LoginRequest;
import com.apitester.api_tester_backend.dto.request.RegisterRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class EnvironmentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldCreateEnvironment() throws Exception {

        String token = getToken();

        EnvironmentRequest request = EnvironmentRequest.builder()
                .name("Development")
                .variables(Map.of(
                        "baseUrl",
                        "https://api.example.com"))
                .build();

        mockMvc.perform(
                post("/api/environments")
                        .header(
                                "Authorization",
                                "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldGetAllEnvironments() throws Exception {

        String token = getToken();

        mockMvc.perform(
                get("/api/environments")
                        .header(
                                "Authorization",
                                "Bearer " + token)
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetEnvironmentById() throws Exception {

        String token = getToken();

        EnvironmentRequest request = EnvironmentRequest.builder()
                .name("Get Test")
                .variables(Map.of(
                        "baseUrl",
                        "https://api.example.com"))
                .build();

        String responseJson = mockMvc.perform(
                post("/api/environments")
                        .header(
                                "Authorization",
                                "Bearer " + token)
                        .contentType(
                                MediaType.APPLICATION_JSON)
                        .content(
                                objectMapper.writeValueAsString(
                                        request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode environment = objectMapper.readTree(responseJson);

        Long environmentId = environment.get("id").asLong();

        mockMvc.perform(
                get("/api/environments/"
                        + environmentId)
                        .header(
                                "Authorization",
                                "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void shouldUpdateEnvironment() throws Exception {

        String token = getToken();

        EnvironmentRequest createRequest = EnvironmentRequest.builder()
                .name("Before Update")
                .variables(Map.of(
                        "baseUrl",
                        "https://api.example.com"))
                .build();

        String responseJson = mockMvc.perform(
                post("/api/environments")
                        .header(
                                "Authorization",
                                "Bearer " + token)
                        .contentType(
                                MediaType.APPLICATION_JSON)
                        .content(
                                objectMapper.writeValueAsString(
                                        createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode environment = objectMapper.readTree(responseJson);

        Long environmentId = environment.get("id").asLong();

        EnvironmentRequest updateRequest = EnvironmentRequest.builder()
                .name("After Update")
                .variables(Map.of(
                        "baseUrl",
                        "https://production.example.com"))
                .build();

        mockMvc.perform(
                put("/api/environments/"
                        + environmentId)
                        .header(
                                "Authorization",
                                "Bearer " + token)
                        .contentType(
                                MediaType.APPLICATION_JSON)
                        .content(
                                objectMapper.writeValueAsString(
                                        updateRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDeleteEnvironment() throws Exception {

        String token = getToken();

        EnvironmentRequest request = EnvironmentRequest.builder()
                .name("Delete Test")
                .variables(Map.of(
                        "baseUrl",
                        "https://api.example.com"))
                .build();

        String responseJson = mockMvc.perform(
                post("/api/environments")
                        .header(
                                "Authorization",
                                "Bearer " + token)
                        .contentType(
                                MediaType.APPLICATION_JSON)
                        .content(
                                objectMapper.writeValueAsString(
                                        request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode environment = objectMapper.readTree(responseJson);

        Long environmentId = environment.get("id").asLong();
        mockMvc.perform(
                delete("/api/environments/"
                        + environmentId)
                        .header(
                                "Authorization",
                                "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    private String getToken() throws Exception {

        String email = "integration"
                + System.nanoTime()
                + "@test.com";

        RegisterRequest registerRequest = RegisterRequest.builder()
                .name("Integration Test User")
                .email(email)
                .password("Password@123")
                .build();

        mockMvc.perform(
                post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                objectMapper.writeValueAsString(
                                        registerRequest)))
                .andExpect(status().isCreated());

        LoginRequest loginRequest = LoginRequest.builder()
                .email(email)
                .password("Password@123")
                .build();

        String authResponseJson = mockMvc.perform(
                post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                objectMapper.writeValueAsString(
                                        loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode authResponse = objectMapper.readTree(authResponseJson);

        return authResponse.get("token").asText();
    }
}