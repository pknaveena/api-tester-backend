package com.apitester.api_tester_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.apitester.api_tester_backend.entity.Collection;
import com.apitester.api_tester_backend.entity.User;

public interface CollectionRepository extends JpaRepository<Collection, Long>{
    
    List<Collection> findByUser(User user);
    Optional<Collection> findByIdAndUser(
            Long id,
            User user
    );
}
