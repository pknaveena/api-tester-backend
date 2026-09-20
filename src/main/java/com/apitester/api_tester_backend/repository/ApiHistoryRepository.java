package com.apitester.api_tester_backend.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.apitester.api_tester_backend.entity.ApiHistory;
import com.apitester.api_tester_backend.entity.User;


public interface ApiHistoryRepository extends JpaRepository<ApiHistory, Long> {
    
    Page<ApiHistory> findByUser(User user, Pageable pageable);

     Optional<ApiHistory> findByIdAndUser(
            Long id,
            User user
    );
}
