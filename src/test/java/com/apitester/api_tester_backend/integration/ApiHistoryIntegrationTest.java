package com.apitester.api_tester_backend.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.apitester.api_tester_backend.dto.request.LoginRequest;
import com.apitester.api_tester_backend.dto.request.RegisterRequest;
import com.apitester.api_tester_backend.entity.ApiHistory;
import com.apitester.api_tester_backend.entity.User;
import com.apitester.api_tester_backend.entity.enums.HttpMethod;
import com.apitester.api_tester_backend.repository.ApiHistoryRepository;
import com.apitester.api_tester_backend.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class ApiHistoryIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApiHistoryRepository apiHistoryRepository;

    @Autowired
    private UserRepository userRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldGetHistory() throws Exception {

        String email = "integration"
                + System.nanoTime()
                + "@test.com";

        String token = getToken(email);

        User user = userRepository.findByEmail(email)
                .orElseThrow();

        createHistory(user);

        mockMvc.perform(
                get("/api/history")
                        .header(
                                "Authorization",
                                "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetHistoryById() throws Exception {

        String email = "integration"
                + System.nanoTime()
                + "@test.com";

        String token = getToken(email);

        User user = userRepository.findByEmail(email)
                .orElseThrow();

        ApiHistory history = createHistory(user);

        mockMvc.perform(
                get("/api/history/" + history.getId())
                        .header(
                                "Authorization",
                                "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDeleteHistory() throws Exception {

        String email = "integration"
                + System.nanoTime()
                + "@test.com";

        String token = getToken(email);

        User user = userRepository.findByEmail(email)
                .orElseThrow();

        ApiHistory history = createHistory(user);

        mockMvc.perform(
                delete("/api/history/" + history.getId())
                        .header(
                                "Authorization",
                                "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    private ApiHistory createHistory(User user) {

        ApiHistory history = new ApiHistory();

        history.setRequestId(
                "request-" + System.nanoTime());

        history.setMethod(HttpMethod.GET);

        history.setUrl(
                "https://example.com/api/test");

        history.setRequestBody(
                "");

        history.setRequestHeaders(
                "{}");

        history.setResponseBody(
                "{\"message\":\"success\"}");

        history.setStatusCode(200);

        history.setResponseTime(100L);

        history.setResponseSize(25L);

        history.setUser(user);

        return apiHistoryRepository.save(history);
    }

    private String getToken(String email) throws Exception {

        RegisterRequest registerRequest = RegisterRequest.builder()
                .name("Integration Test User")
                .email(email)
                .password("Password@123")
                .build();

        mockMvc.perform(
                post("/api/auth/register")
                        .contentType(
                                MediaType.APPLICATION_JSON)
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
                        .contentType(
                                MediaType.APPLICATION_JSON)
                        .content(
                                objectMapper.writeValueAsString(
                                        loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode authResponse = objectMapper.readTree(
                authResponseJson);

        return authResponse
                .get("token")
                .asText();
    }
}
