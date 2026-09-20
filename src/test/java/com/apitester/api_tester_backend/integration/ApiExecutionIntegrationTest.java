package com.apitester.api_tester_backend.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.apitester.api_tester_backend.dto.request.ApiExecuteRequest;
import com.apitester.api_tester_backend.entity.enums.HttpMethod;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class ApiExecutionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldExecuteGetRequest() throws Exception {

        String token = getToken();

        ApiExecuteRequest request = ApiExecuteRequest.builder()
                .method(HttpMethod.GET)
                .url("https://httpbin.org/get")
                .queryParams(Map.of(
                        "page", "1",
                        "limit", "10"))
                .build();

        MvcResult result = mockMvc.perform(
                post("/api/requests/execute")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson =
                result.getResponse().getContentAsString();

        JsonNode response =
                objectMapper.readTree(responseJson);


        // Verify our API returned the expected response fields
        assert response.has("statusCode");
        assert response.has("statusText");
        assert response.has("headers");
        assert response.has("body");
        assert response.has("responseTime");
        assert response.has("responseSize");

        assert response.get("statusCode").asInt() == 200;
    }

    private String getToken() throws Exception {

        String email =
                "integration" + System.nanoTime() + "@test.com";

        // Register
        String registerJson = """
                {
                    "name": "Integration Test User",
                    "email": "%s",
                    "password": "password123"
                }
                """.formatted(email);

        mockMvc.perform(
                post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andExpect(status().isCreated());

        // Login
        String loginJson = """
                {
                    "email": "%s",
                    "password": "password123"
                }
                """.formatted(email);

        String authResponseJson = mockMvc.perform(
                post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract JWT
        JsonNode authResponse =
                objectMapper.readTree(authResponseJson);

        return authResponse.get("token").asText();
    }
}