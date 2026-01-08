package com.Chatop.API.controller;

import com.Chatop.API.dto.auth.AuthResponse;
import com.Chatop.API.dto.auth.LoginRequest;
import com.Chatop.API.dto.auth.RegisterRequest;
import com.Chatop.API.dto.user.UserResponse;
import com.Chatop.API.model.User;
import com.Chatop.API.repository.UserRepository;
import com.Chatop.API.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/me")
    public UserResponse me(Authentication authentication) {
        String email = authentication.getName();
        User u = userRepository.findByEmail(email).orElseThrow();
        return UserResponse.builder()
                .id(u.getId())
                .name(u.getName())
                .email(u.getEmail())
                .createdAt(u.getCreatedAt())
                .updatedAt(u.getUpdatedAt())
                .build();
    }
}
