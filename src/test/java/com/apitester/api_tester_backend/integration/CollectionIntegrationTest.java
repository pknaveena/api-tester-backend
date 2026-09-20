package com.apitester.api_tester_backend.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.apitester.api_tester_backend.dto.request.CollectionRequest;
import com.apitester.api_tester_backend.dto.request.LoginRequest;
import com.apitester.api_tester_backend.dto.request.RegisterRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class CollectionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldCreateCollection() throws Exception {

        String token = getToken();

        CollectionRequest request = CollectionRequest.builder()
                .name("Test Collection")
                .description("Integration test collection")
                .build();

        mockMvc.perform(
                post("/api/collections")
                        .header(
                                "Authorization",
                                "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                objectMapper.writeValueAsString(
                                        request)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldGetAllCollections() throws Exception {

        String token = getToken();

        mockMvc.perform(
                get("/api/collections")
                        .header(
                                "Authorization",
                                "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetCollectionById() throws Exception {

        String token = getToken();

        CollectionRequest request = CollectionRequest.builder()
                .name("Get Test Collection")
                .description("Get by ID test")
                .build();

        String responseJson = mockMvc.perform(
                post("/api/collections")
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

        JsonNode collection = objectMapper.readTree(responseJson);

        Long collectionId = collection.get("id").asLong();

        mockMvc.perform(
                get("/api/collections/"
                        + collectionId)
                        .header(
                                "Authorization",
                                "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void shouldUpdateCollection() throws Exception {

        String token = getToken();

        CollectionRequest createRequest = CollectionRequest.builder()
                .name("Before Update")
                .description("Old description")
                .build();

        String responseJson = mockMvc.perform(
                post("/api/collections")
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

        JsonNode collection = objectMapper.readTree(responseJson);

        Long collectionId = collection.get("id").asLong();

        CollectionRequest updateRequest = CollectionRequest.builder()
                .name("After Update")
                .description("Updated description")
                .build();

        mockMvc.perform(
                put("/api/collections/"
                        + collectionId)
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
    void shouldDeleteCollection() throws Exception {

        String token = getToken();

        CollectionRequest request = CollectionRequest.builder()
                .name("Delete Test Collection")
                .description("Collection to delete")
                .build();

        String responseJson = mockMvc.perform(
                post("/api/collections")
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

        JsonNode collection = objectMapper.readTree(responseJson);

        Long collectionId = collection.get("id").asLong();

        mockMvc.perform(
                delete("/api/collections/"
                        + collectionId)
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

        JsonNode authResponse = objectMapper.readTree(authResponseJson);

        return authResponse
                .get("token")
                .asText();
    }
}