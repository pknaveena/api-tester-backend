package com.apitester.api_tester_backend.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
class AuthIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @Test
        void shouldRegisterLogin() throws Exception {

                String email = "integration" + System.nanoTime() + "@test.com";
                String password = "password123";

                String registerJson = """
                                {
                                    "name": "Integration Test User",
                                    "email": "%s",
                                    "password": "%s"
                                }
                                """.formatted(email, password);

                // 1. Register user
                mockMvc.perform(
                                post("/api/auth/register")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(registerJson))
                                .andExpect(status().isCreated());

                // 2. Login with the same credentials
                mockMvc.perform(
                                post("/api/auth/login")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content("""
                                                                {
                                                                    "email": "%s",
                                                                    "password": "%s"
                                                                }
                                                                """.formatted(email, password)))
                                .andExpect(status().isOk())
                                .andReturn();
        }

}