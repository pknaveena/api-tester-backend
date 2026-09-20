package com.apitester.api_tester_backend.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.apitester.api_tester_backend.dto.request.EnvironmentRequest;
import com.apitester.api_tester_backend.dto.response.EnvironmentResponse;
import com.apitester.api_tester_backend.service.EnvironmentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/environments")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class EnvironmentController {

    private final EnvironmentService environmentService;

    @Operation(summary = "Create environment", description = "Creates a new environment for the authenticated user")
    @PostMapping
    public ResponseEntity<EnvironmentResponse> create(@Valid @RequestBody EnvironmentRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(environmentService.create(request));
    }

    @Operation(summary = "Get all environments", description = "Returns the authenticated user's environments with pagination")
    @GetMapping
    public ResponseEntity<Page<EnvironmentResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(
                environmentService.getAll(PageRequest.of(page, size)));
    }

    @Operation(summary = "Get environment by ID", description = "Returns a specific environment owned by the authenticated user")
    @GetMapping("/{id}")
    public ResponseEntity<EnvironmentResponse> getById(@PathVariable Long id) {

        return ResponseEntity.ok(
                environmentService.getById(id));
    }

    @Operation(summary = "Update environment", description = "Updates an existing environment owned by the authenticated user")
    @PutMapping("/{id}")
    public ResponseEntity<EnvironmentResponse> update(
            @PathVariable Long id, @Valid @RequestBody EnvironmentRequest request) {

        return ResponseEntity.ok(
                environmentService.update(id, request));
    }

    @Operation(summary = "Delete environment", description = "Deletes an environment owned by the authenticated user")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        environmentService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
