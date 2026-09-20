package com.apitester.api_tester_backend.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.apitester.api_tester_backend.dto.request.CollectionRequest;
import com.apitester.api_tester_backend.dto.response.CollectionResponse;
import com.apitester.api_tester_backend.entity.Collection;
import com.apitester.api_tester_backend.entity.User;
import com.apitester.api_tester_backend.exception.ApiException;
import com.apitester.api_tester_backend.repository.CollectionRepository;
import com.apitester.api_tester_backend.util.SecurityUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CollectionService {

    private final CollectionRepository collectionRepository;
    private final SecurityUtil securityUtil;

    public CollectionResponse create(CollectionRequest request) {

        User user = securityUtil.getCurrentUser();

        Collection collection = Collection.builder()
                .name(request.getName())
                .description(request.getDescription())
                .user(user)
                .build();

        return toResponse(collectionRepository.save(collection));
    }

    public List<CollectionResponse> getAll() {

        User user = securityUtil.getCurrentUser();

        return collectionRepository
                .findByUser(user)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public CollectionResponse getById(Long id) {

        Collection collection = getOwnedCollection(id);

        return toResponse(collection);
    }

    public void delete(Long id) {

        Collection collection = getOwnedCollection(id);

        collectionRepository.delete(collection);
    }

    public CollectionResponse update(
            Long id,
            CollectionRequest request) {

        Collection collection = getOwnedCollection(id);

        collection.setName(request.getName());
        collection.setDescription(
                request.getDescription());

        return toResponse(
                collectionRepository.save(collection));
    }

    private Collection getOwnedCollection(Long id) {
        User user = securityUtil.getCurrentUser();

        Collection collection = collectionRepository
                .findByIdAndUser(id, user)
                .orElseThrow(() -> new ApiException(
                        "Collection not found",
                        HttpStatus.NOT_FOUND));

        return collection;
    }

    private CollectionResponse toResponse(Collection collection) {

        return CollectionResponse.builder()
                .id(collection.getId())
                .name(collection.getName())
                .description(collection.getDescription())
                .createdAt(collection.getCreatedAt())
                .build();
    }
}
