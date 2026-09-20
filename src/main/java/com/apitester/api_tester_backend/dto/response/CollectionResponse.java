package com.apitester.api_tester_backend.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter

@NoArgsConstructor
@AllArgsConstructor 
@Builder
public class CollectionResponse {

    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
}