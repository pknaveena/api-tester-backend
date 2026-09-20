package com.apitester.api_tester_backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.apitester.api_tester_backend.dto.request.CollectionRequest;
import com.apitester.api_tester_backend.dto.response.CollectionResponse;
import com.apitester.api_tester_backend.service.CollectionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/collections")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class CollectionController {

    private final CollectionService collectionService;

     @Operation(
        summary = "Create collection",
        description = "Creates a new collection for the authenticated user"
    )
    @PostMapping
    public ResponseEntity<CollectionResponse> create(@Valid @RequestBody CollectionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
         .body(collectionService.create(request));
    }

     @Operation(
        summary = "Get all collections",
        description = "Returns all collections belonging to the authenticated user"
    )
    @GetMapping
    public ResponseEntity<List<CollectionResponse>> getAll() {

        return ResponseEntity.ok(
                collectionService.getAll());
    }

     @Operation(
        summary = "Get collection by ID",
        description = "Returns a specific collection owned by the authenticated user"
    )
    @GetMapping("/{id}")
    public ResponseEntity<CollectionResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(
                collectionService.getById(id));
    }

     @Operation(
        summary = "Update collection",
        description = "Updates an existing collection owned by the authenticated user"
    )
    @PutMapping("/{id}")
    public ResponseEntity<CollectionResponse> update(
            @PathVariable Long id,@Valid @RequestBody CollectionRequest request) {

        return ResponseEntity.ok(
                collectionService.update(id,request));
    }

     @Operation(
        summary = "Delete collection",
        description = "Deletes a collection owned by the authenticated user"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        collectionService.delete(id);

        return ResponseEntity.noContent().build(); 
    }

}
