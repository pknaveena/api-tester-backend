package com.apitester.api_tester_backend.util;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.apitester.api_tester_backend.exception.ApiException;

import lombok.RequiredArgsConstructor;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class JsonUtil {

    private final ObjectMapper objectMapper;

    public String toJson(Map<String, String> variables) {
        try {
            return objectMapper.writeValueAsString(variables);
        } catch (JacksonException e) {
            throw new ApiException(
                    "Failed to serialize environment variables",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Map<String, String> fromJson(String json) {
        try {
            return objectMapper.readValue(
                    json,
                    new TypeReference<Map<String, String>>() {}
            );
        } catch (JacksonException e) {
            throw new ApiException(
                    "Failed to deserialize environment variables",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
