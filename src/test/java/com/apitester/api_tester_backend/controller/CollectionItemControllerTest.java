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

import com.apitester.api_tester_backend.dto.request.CollectionItemExecuteRequest;
import com.apitester.api_tester_backend.dto.request.CollectionItemRequest;
import com.apitester.api_tester_backend.dto.response.ApiExecuteResponse;
import com.apitester.api_tester_backend.dto.response.CollectionItemResponse;
import com.apitester.api_tester_backend.entity.enums.HttpMethod;
import com.apitester.api_tester_backend.security.CustomUserDetailsService;
import com.apitester.api_tester_backend.security.JwtService;
import com.apitester.api_tester_backend.service.CollectionItemService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(CollectionItemController.class)
@AutoConfigureMockMvc(addFilters = false)
class CollectionItemControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private JwtService jwtService;

        @MockitoBean
        private CustomUserDetailsService userDetailsService;

        @MockitoBean
        private CollectionItemService collectionItemService;

        private final ObjectMapper objectMapper = new ObjectMapper();

        @Test
        void shouldCreateCollectionItem() throws Exception {

                CollectionItemRequest request = CollectionItemRequest.builder()
                                .name("Get Users")
                                .method(HttpMethod.GET)
                                .url("https://api.example.com/users")
                                .build();

                CollectionItemResponse response = CollectionItemResponse.builder()
                                .id(1L)
                                .name("Get Users")
                                .build();

                when(collectionItemService.create(
                                eq(10L),
                                any(CollectionItemRequest.class)))
                                .thenReturn(response);

                mockMvc.perform(
                                post("/api/collections/10/items")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.name")
                                                .value("Get Users"));

                verify(collectionItemService).create(
                                eq(10L),
                                any(CollectionItemRequest.class));
        }

        @Test
        void shouldGetCollectionItems() throws Exception {

                CollectionItemResponse response = CollectionItemResponse.builder()
                                .id(1L)
                                .name("Get Users")
                                .build();

                when(collectionItemService.getAll(10L))
                                .thenReturn(List.of(response));

                mockMvc.perform(
                                get("/api/collections/10/items"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].id").value(1))
                                .andExpect(jsonPath("$[0].name")
                                                .value("Get Users"));

                verify(collectionItemService).getAll(10L);
        }

        @Test
        void shouldGetCollectionItemById() throws Exception {

                CollectionItemResponse response = CollectionItemResponse.builder()
                                .id(1L)
                                .name("Get Users")
                                .build();

                when(collectionItemService.getById(10L, 1L))
                                .thenReturn(response);

                mockMvc.perform(
                                get("/api/collections/10/items/1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1));

                verify(collectionItemService)
                                .getById(10L, 1L);
        }

        @Test
        void shouldUpdateCollectionItem() throws Exception {

                CollectionItemRequest request = CollectionItemRequest.builder()
                                .name("Updated Request")
                                .method(HttpMethod.GET)
                                .url("https://api.example.com/users")
                                .build();

                CollectionItemResponse response = CollectionItemResponse.builder()
                                .id(1L)
                                .name("Updated Request")
                                .build();

                when(collectionItemService.update(
                                eq(10L),
                                eq(1L),
                                any(CollectionItemRequest.class)))
                                .thenReturn(response);

                mockMvc.perform(
                                put("/api/collections/10/items/1")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.name")
                                                .value("Updated Request"));

                verify(collectionItemService).update(
                                eq(10L),
                                eq(1L),
                                any(CollectionItemRequest.class));
        }

        @Test
        void shouldDeleteCollectionItem() throws Exception {

                doNothing()
                                .when(collectionItemService)
                                .delete(10L, 1L);

                mockMvc.perform(
                                delete("/api/collections/10/items/1"))
                                .andExpect(status().isNoContent());

                verify(collectionItemService)
                                .delete(10L, 1L);
        }

        @Test
        void shouldExecuteCollectionItem() throws Exception {

                CollectionItemExecuteRequest request = CollectionItemExecuteRequest.builder()
                                .environmentId(1L)
                                .build();

                ApiExecuteResponse response = ApiExecuteResponse.builder()
                                .statusCode(200)
                                .statusText("OK")
                                .body("{\"message\":\"success\"}")
                                .build();

                when(collectionItemService.execute(
                                eq(10L),
                                eq(1L),
                                any(CollectionItemExecuteRequest.class)))
                                .thenReturn(response);

                mockMvc.perform(
                                post("/api/collections/10/items/1/execute")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.statusCode").value(200))
                                .andExpect(jsonPath("$.statusText").value("OK"));

                verify(collectionItemService).execute(
                                eq(10L),
                                eq(1L),
                                any(CollectionItemExecuteRequest.class));
        }
}