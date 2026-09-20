package com.apitester.api_tester_backend.util;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.apitester.api_tester_backend.entity.User;
import com.apitester.api_tester_backend.exception.ApiException;
import com.apitester.api_tester_backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SecurityUtil {

    private final UserRepository userRepository;

    public User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            throw new ApiException(
                    "User is not authenticated",
                    HttpStatus.UNAUTHORIZED
            );
        }

        String email = authentication.getName();

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new ApiException(
                                "User not found",
                                HttpStatus.NOT_FOUND
                        )
                );
    }
}