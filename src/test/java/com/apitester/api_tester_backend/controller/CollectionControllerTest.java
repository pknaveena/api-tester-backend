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

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.apitester.api_tester_backend.dto.request.CollectionRequest;
import com.apitester.api_tester_backend.dto.response.CollectionResponse;
import com.apitester.api_tester_backend.security.CustomUserDetailsService;
import com.apitester.api_tester_backend.security.JwtService;
import com.apitester.api_tester_backend.service.CollectionService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(CollectionController.class)
@AutoConfigureMockMvc(addFilters = false)
class CollectionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @MockitoBean
    private CollectionService collectionService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Test
    void shouldCreateCollection() throws Exception {

        CollectionRequest request = CollectionRequest.builder()
                .name("My APIs")
                .description("API collection")
                .build();

        CollectionResponse response = CollectionResponse.builder()
                .id(1L)
                .name("My APIs")
                .description("API collection")
                .build();

        when(collectionService.create(any(CollectionRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                post("/api/collections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("My APIs"));

        verify(collectionService)
                .create(any(CollectionRequest.class));
    }

    @Test
    void shouldGetAllCollections() throws Exception {

        CollectionResponse response = CollectionResponse.builder()
                .id(1L)
                .name("My APIs")
                .description("API collection")
                .build();

        when(collectionService.getAll())
                .thenReturn(List.of(response));

        mockMvc.perform(
                get("/api/collections"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name")
                        .value("My APIs"));

        verify(collectionService).getAll();
    }

    @Test
    void shouldGetCollectionById() throws Exception {

        CollectionResponse response = CollectionResponse.builder()
                .id(1L)
                .name("My APIs")
                .description("API collection")
                .build();

        when(collectionService.getById(1L))
                .thenReturn(response);

        mockMvc.perform(
                get("/api/collections/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name")
                        .value("My APIs"));

        verify(collectionService).getById(1L);
    }

    @Test
    void shouldUpdateCollection() throws Exception {

        CollectionRequest request = CollectionRequest.builder()
                .name("Updated APIs")
                .description("Updated description")
                .build();

        CollectionResponse response = CollectionResponse.builder()
                .id(1L)
                .name("Updated APIs")
                .description("Updated description")
                .build();

        when(collectionService.update(
                eq(1L),
                any(CollectionRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                put("/api/collections/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name")
                        .value("Updated APIs"));

        verify(collectionService).update(
                eq(1L),
                any(CollectionRequest.class));
    }

    @Test
    void shouldDeleteCollection() throws Exception {

        doNothing()
                .when(collectionService)
                .delete(1L);

        mockMvc.perform(
                delete("/api/collections/1"))
                .andExpect(status().isNoContent());

        verify(collectionService).delete(1L);
    }
}