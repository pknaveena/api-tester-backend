package com.apitester.api_tester_backend.dto.request;

import java.util.Map;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor 
@NoArgsConstructor 
@Builder 
public class EnvironmentRequest {

    @NotBlank
    private String name;

    @NotEmpty
    private Map<String, String> variables;
}