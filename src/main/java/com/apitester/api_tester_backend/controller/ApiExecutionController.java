package com.apitester.api_tester_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.apitester.api_tester_backend.dto.request.ApiExecuteRequest;
import com.apitester.api_tester_backend.dto.response.ApiExecuteResponse;
import com.apitester.api_tester_backend.service.ApiExecutionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/requests")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class ApiExecutionController {

    private final ApiExecutionService apiExecutionService;

    @Operation(
        summary = "Execute an API request",
        description = "Executes an HTTP request against the target API"
)
    @PostMapping("/execute")
    public ResponseEntity<ApiExecuteResponse> execute(
            @Valid
            @RequestBody
            ApiExecuteRequest request) {

        return ResponseEntity.ok(
                apiExecutionService.execute(request)
        );
    }
}