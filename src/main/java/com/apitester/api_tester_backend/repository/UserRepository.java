package com.apitester.api_tester_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.apitester.api_tester_backend.entity.User;

public interface UserRepository  extends JpaRepository<User,Long>{
    

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
