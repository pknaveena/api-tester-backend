package com.apitester.api_tester_backend.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.apitester.api_tester_backend.dto.response.HistoryResponse;
import com.apitester.api_tester_backend.security.CustomUserDetailsService;
import com.apitester.api_tester_backend.security.JwtService;
import com.apitester.api_tester_backend.service.ApiHistoryService;

@WebMvcTest(ApiHistoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class ApiHistoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ApiHistoryService apiHistoryService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;


    @Test
    void shouldGetHistory() throws Exception {

        HistoryResponse response = HistoryResponse.builder()
                .id(1L)
                .build();

        PageImpl<HistoryResponse> page =
                new PageImpl<>(
                        List.of(response),
                        PageRequest.of(0, 20),
                        1);

        when(apiHistoryService.getHistory(any(PageRequest.class)))
                .thenReturn(page);

        mockMvc.perform(
                get("/api/history")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id")
                        .value(1));

        verify(apiHistoryService)
                .getHistory(any(PageRequest.class));
    }

    @Test
    void shouldGetHistoryById() throws Exception {

        HistoryResponse response = HistoryResponse.builder()
                .id(1L)
                .build();

        when(apiHistoryService.getHistoryById(1L))
                .thenReturn(response);

        mockMvc.perform(
                get("/api/history/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(apiHistoryService)
                .getHistoryById(1L);
    }

    @Test
    void shouldDeleteHistory() throws Exception {

        doNothing()
                .when(apiHistoryService)
                .deleteHistory(1L);

        mockMvc.perform(
                delete("/api/history/1"))
                .andExpect(status().isNoContent());

        verify(apiHistoryService)
                .deleteHistory(1L);
    }
}