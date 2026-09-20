package com.apitester.api_tester_backend.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import com.apitester.api_tester_backend.dto.request.CollectionItemExecuteRequest;
import com.apitester.api_tester_backend.dto.request.CollectionItemRequest;
import com.apitester.api_tester_backend.dto.request.CollectionRequest;
import com.apitester.api_tester_backend.dto.request.EnvironmentRequest;
import com.apitester.api_tester_backend.dto.request.LoginRequest;
import com.apitester.api_tester_backend.dto.request.RegisterRequest;
import com.apitester.api_tester_backend.entity.enums.HttpMethod;
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
class CollectionItemIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldCreateCollectionItem() throws Exception {

        String token = getToken();

        Long collectionId = createCollection(token);

        CollectionItemRequest request = CollectionItemRequest.builder()
                .name("Get Users")
                .method(HttpMethod.GET)
                .url("https://example.com/users")
                .build();

        mockMvc.perform(
                post("/api/collections/"
                        + collectionId
                        + "/items")
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
    void shouldGetAllCollectionItems() throws Exception {

        String token = getToken();

        Long collectionId = createCollection(token);

        CollectionItemRequest request = CollectionItemRequest.builder()
                .name("Get Users")
                .method(HttpMethod.GET)
                .url("https://example.com/users")
                .build();

        mockMvc.perform(
                post("/api/collections/"
                        + collectionId
                        + "/items")
                        .header(
                                "Authorization",
                                "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                objectMapper.writeValueAsString(
                                        request)))
                .andExpect(status().isCreated());

        mockMvc.perform(
                get("/api/collections/"
                        + collectionId
                        + "/items")
                        .header(
                                "Authorization",
                                "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetCollectionItemById() throws Exception {

        String token = getToken();

        Long collectionId = createCollection(token);

        CollectionItemRequest request = CollectionItemRequest.builder()
                .name("Get User")
                .method(HttpMethod.GET)
                .url("https://example.com/users/1")
                .build();

        String responseJson = mockMvc.perform(
                post("/api/collections/"
                        + collectionId
                        + "/items")
                        .header(
                                "Authorization",
                                "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                objectMapper.writeValueAsString(
                                        request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode item = objectMapper.readTree(responseJson);

        Long itemId = item.get("id").asLong();

        mockMvc.perform(
                get("/api/collections/"
                        + collectionId
                        + "/items/"
                        + itemId)
                        .header(
                                "Authorization",
                                "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void shouldUpdateCollectionItem() throws Exception {

        String token = getToken();

        Long collectionId = createCollection(token);

        CollectionItemRequest createRequest = CollectionItemRequest.builder()
                .name("Before Update")
                .method(HttpMethod.GET)
                .url("https://example.com/users")
                .build();

        String responseJson = mockMvc.perform(
                post("/api/collections/"
                        + collectionId
                        + "/items")
                        .header(
                                "Authorization",
                                "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                objectMapper.writeValueAsString(
                                        createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode item = objectMapper.readTree(responseJson);

        Long itemId = item.get("id").asLong();

        CollectionItemRequest updateRequest = CollectionItemRequest.builder()
                .name("After Update")
                .method(HttpMethod.POST)
                .url("https://example.com/users")
                .body("""
                        {
                            "name": "John"
                        }
                        """)
                .build();

        mockMvc.perform(
                put("/api/collections/"
                        + collectionId
                        + "/items/"
                        + itemId)
                        .header(
                                "Authorization",
                                "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                objectMapper.writeValueAsString(
                                        updateRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDeleteCollectionItem() throws Exception {

        String token = getToken();

        Long collectionId = createCollection(token);

        CollectionItemRequest request = CollectionItemRequest.builder()
                .name("Delete Test Item")
                .method(HttpMethod.GET)
                .url("https://example.com/users")
                .build();

        String responseJson = mockMvc.perform(
                post("/api/collections/"
                        + collectionId
                        + "/items")
                        .header(
                                "Authorization",
                                "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                objectMapper.writeValueAsString(
                                        request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode item = objectMapper.readTree(responseJson);

        Long itemId = item.get("id").asLong();

        mockMvc.perform(
                delete("/api/collections/"
                        + collectionId
                        + "/items/"
                        + itemId)
                        .header(
                                "Authorization",
                                "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    private Long createCollection(String token) throws Exception {

        CollectionRequest request = CollectionRequest.builder()
                .name("Integration Test Collection "
                        + System.nanoTime())
                .description("Collection for integration testing")
                .build();

        String responseJson = mockMvc.perform(
                post("/api/collections")
                        .header(
                                "Authorization",
                                "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                objectMapper.writeValueAsString(
                                        request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode collection = objectMapper.readTree(responseJson);

        return collection.get("id").asLong();
    }

@Test
void shouldExecuteCollectionItem() throws Exception {

        String token = getToken();

        Long collectionId = createCollection(token);

        // Create environment
        EnvironmentRequest environmentRequest = EnvironmentRequest.builder()
                        .name("Test Environment " + System.nanoTime())
                        .variables(Map.of(
                                        "baseUrl",
                                        "https://httpbin.org"))
                        .build();

        String environmentResponseJson = mockMvc.perform(
                        post("/api/environments")
                                        .header(
                                                        "Authorization",
                                                        "Bearer " + token)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(
                                                        objectMapper.writeValueAsString(
                                                                        environmentRequest)))
                        .andExpect(status().isCreated())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        JsonNode environment = objectMapper.readTree(
                        environmentResponseJson);

        Long environmentId = environment.get("id").asLong();

        // Create collection item
        CollectionItemRequest itemRequest = CollectionItemRequest.builder()
                        .name("Get Users")
                        .method(HttpMethod.GET)
                        .url("https://httpbin.org/get")
                        .build();

        String itemResponseJson = mockMvc.perform(
                        post("/api/collections/"
                                        + collectionId
                                        + "/items")
                                        .header(
                                                        "Authorization",
                                                        "Bearer " + token)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(
                                                        objectMapper.writeValueAsString(
                                                                        itemRequest)))
                        .andExpect(status().isCreated())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        JsonNode item = objectMapper.readTree(itemResponseJson);

        Long itemId = item.get("id").asLong();

        // Execute collection item
        CollectionItemExecuteRequest executeRequest =
                        CollectionItemExecuteRequest.builder()
                                        .environmentId(environmentId)
                                        .build();

        mockMvc.perform(
                        post("/api/collections/"
                                        + collectionId
                                        + "/items/"
                                        + itemId
                                        + "/execute")
                                        .header(
                                                        "Authorization",
                                                        "Bearer " + token)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(
                                                        objectMapper.writeValueAsString(
                                                                        executeRequest)))
                        .andExpect(status().isOk());
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

        return authResponse
                .get("token")
                .asText();
    }
}
