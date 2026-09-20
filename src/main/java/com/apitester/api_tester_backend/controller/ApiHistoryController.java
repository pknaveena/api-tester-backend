package com.apitester.api_tester_backend.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.apitester.api_tester_backend.dto.response.HistoryResponse;
import com.apitester.api_tester_backend.service.ApiHistoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/history")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class ApiHistoryController {

    private final ApiHistoryService apiHistoryService;

    @Operation(
        summary = "Get API history",
        description = "Returns the authenticated user's API execution history with pagination"
    )
    @GetMapping
    public ResponseEntity<Page<HistoryResponse>> getHistory(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(
                apiHistoryService.getHistory(
                        pageable));
    }

    @Operation(
        summary = "Get history entry",
        description = "Returns a specific API execution history entry by ID"
    )
    @GetMapping("/{id}")
    public ResponseEntity<HistoryResponse> getHistoryById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                apiHistoryService.getHistoryById(id)
        );
    }

    @Operation(
        summary = "Delete history entry",
        description = "Deletes a specific API execution history entry by ID"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHistory(
            @PathVariable Long id) {

        apiHistoryService.deleteHistory(
                id);

        return ResponseEntity.noContent()
                .build();
    }

}
