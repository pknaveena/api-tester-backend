package com.apitester.api_tester_backend.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.apitester.api_tester_backend.dto.request.ApiExecuteRequest;
import com.apitester.api_tester_backend.dto.response.ApiExecuteResponse;
import com.apitester.api_tester_backend.entity.enums.HttpMethod;
import com.apitester.api_tester_backend.security.CustomUserDetailsService;
import com.apitester.api_tester_backend.security.JwtService;
import com.apitester.api_tester_backend.service.ApiExecutionService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ApiExecutionController.class)
@AutoConfigureMockMvc(addFilters = false)
class ApiExecutionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @MockitoBean
    private ApiExecutionService apiExecutionService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void shouldExecuteApiRequest() throws Exception {

        ApiExecuteRequest request = ApiExecuteRequest.builder()
                .method(HttpMethod.GET)
                .url("https://api.example.com/users")
                .build();

        ApiExecuteResponse response = ApiExecuteResponse.builder()
                .statusCode(200)
                .statusText("OK")
                .body("{\"message\":\"success\"}")
                .build();

        when(apiExecutionService.execute(
                any(ApiExecuteRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                post("/api/requests/execute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode")
                        .value(200))
                .andExpect(jsonPath("$.statusText")
                        .value("OK"));

        verify(apiExecutionService)
                .execute(any(ApiExecuteRequest.class));
    }
}
