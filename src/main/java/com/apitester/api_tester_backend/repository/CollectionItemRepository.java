package com.apitester.api_tester_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.apitester.api_tester_backend.entity.Collection;
import com.apitester.api_tester_backend.entity.CollectionItem;

public interface CollectionItemRepository
        extends JpaRepository<CollectionItem, Long> {

    List<CollectionItem> findByCollection(
            Collection collection
    );

    Optional<CollectionItem> findByIdAndCollection(
            Long id,
            Collection collection
    );
}