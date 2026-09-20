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

import com.apitester.api_tester_backend.dto.request.CollectionItemExecuteRequest;
import com.apitester.api_tester_backend.dto.request.CollectionItemRequest;
import com.apitester.api_tester_backend.dto.response.ApiExecuteResponse;
import com.apitester.api_tester_backend.dto.response.CollectionItemResponse;
import com.apitester.api_tester_backend.service.CollectionItemService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/collections/{collectionId}/items")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class CollectionItemController {

    private final CollectionItemService collectionItemService;

    @Operation(summary = "Create collection item", description = "Creates a new API request inside a collection")
    @PostMapping
    public ResponseEntity<CollectionItemResponse> create(
            @PathVariable Long collectionId,
            @Valid @RequestBody CollectionItemRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        collectionItemService.create(
                                collectionId,
                                request));
    }

    @Operation(summary = "Get collection items", description = "Returns all API requests belonging to a collection")
    @GetMapping
    public ResponseEntity<List<CollectionItemResponse>> getAll(
            @PathVariable Long collectionId) {

        return ResponseEntity.ok(
                collectionItemService.getAll(
                        collectionId));
    }

    @Operation(summary = "Get collection item", description = "Returns a specific API request from a collection")
    @GetMapping("/{itemId}")
    public ResponseEntity<CollectionItemResponse> getById(
            @PathVariable Long collectionId,
            @PathVariable Long itemId) {

        return ResponseEntity.ok(
                collectionItemService.getById(
                        collectionId,
                        itemId));
    }

    @Operation(summary = "Update collection item", description = "Updates an API request inside a collection")
    @PutMapping("/{itemId}")
    public ResponseEntity<CollectionItemResponse> update(
            @PathVariable Long collectionId,
            @PathVariable Long itemId,
            @Valid @RequestBody CollectionItemRequest request) {

        return ResponseEntity.ok(
                collectionItemService.update(
                        collectionId,
                        itemId,
                        request));
    }

    @Operation(summary = "Delete collection item", description = "Deletes an API request from a collection")
    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long collectionId,
            @PathVariable Long itemId) {

        collectionItemService.delete(
                collectionId,
                itemId);

        return ResponseEntity.noContent()
                .build();
    }

    @Operation(summary = "Execute collection item", description = "Executes an API request stored in a collection")
    @PostMapping("/{itemId}/execute")
    public ResponseEntity<ApiExecuteResponse> execute(
            @PathVariable Long collectionId,
            @PathVariable Long itemId,
            @Valid @RequestBody CollectionItemExecuteRequest request) {

        return ResponseEntity.ok(
                collectionItemService.execute(
                        collectionId,
                        itemId,
                        request));
    }
}