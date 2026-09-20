package com.apitester.api_tester_backend.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.apitester.api_tester_backend.entity.Environment;
import com.apitester.api_tester_backend.entity.User;

public interface EnvironmentRepository extends JpaRepository<Environment, Long> {

    Page<Environment> findByUser(
            User user,
            Pageable pageable
    );

    Optional<Environment> findByIdAndUser(
            Long id,
            User user
    );
}