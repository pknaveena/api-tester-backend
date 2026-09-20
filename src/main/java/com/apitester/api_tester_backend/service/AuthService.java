package com.apitester.api_tester_backend.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.apitester.api_tester_backend.dto.request.LoginRequest;
import com.apitester.api_tester_backend.dto.request.RegisterRequest;
import com.apitester.api_tester_backend.dto.response.AuthResponse;
import com.apitester.api_tester_backend.entity.User;
import com.apitester.api_tester_backend.entity.enums.Role;
import com.apitester.api_tester_backend.exception.ApiException;
import com.apitester.api_tester_backend.repository.UserRepository;
import com.apitester.api_tester_backend.security.CustomUserDetailsService;
import com.apitester.api_tester_backend.security.JwtService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final AuthenticationManager authenticationManager;
        private final CustomUserDetailsService userDetailsService;
        private final JwtService jwtService;

        public void register(RegisterRequest request) {

                if (userRepository.existsByEmail(
                                request.getEmail())) {

                        throw new ApiException(
                                        "Email already exists",
                                        HttpStatus.CONFLICT);
                }

                User user = User.builder()
                                .name(request.getName())
                                .email(request.getEmail())
                                .password(
                                                passwordEncoder.encode(
                                                                request.getPassword()))
                                .role(Role.USER)
                                .enabled(true)
                                .build();

                userRepository.save(user);
        }

        public AuthResponse login(LoginRequest request) {

                try {

                        authenticationManager.authenticate(
                                        new UsernamePasswordAuthenticationToken(
                                                        request.getEmail(),
                                                        request.getPassword()));

                } catch (BadCredentialsException ex) {

                        throw new ApiException(
                                        "Invalid email or password",
                                        HttpStatus.UNAUTHORIZED);
                }

                UserDetails userDetails = userDetailsService.loadUserByUsername(
                                request.getEmail());

                String token = jwtService.generateToken(userDetails);

                return AuthResponse.builder()
                                .token(token)
                                .tokenType("Bearer")
                                .expiresIn(3600)
                                .build();
        }

}
