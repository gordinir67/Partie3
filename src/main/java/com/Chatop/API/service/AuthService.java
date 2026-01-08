package com.Chatop.API.service;

import com.Chatop.API.dto.auth.AuthResponse;
import com.Chatop.API.dto.auth.LoginRequest;
import com.Chatop.API.dto.auth.RegisterRequest;
import com.Chatop.API.model.User;
import com.Chatop.API.repository.UserRepository;
import com.Chatop.API.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }

        User u = new User();
        u.setName(req.getName());
        u.setEmail(req.getEmail());
        u.setPassword(passwordEncoder.encode(req.getPassword()));
        User saved = userRepository.save(u);

        String token = jwtService.generateToken(saved.getEmail(), Map.of("userId", saved.getId()));
        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest req) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
        );

        User u = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new BadCredentialsException("error"));

        String token = jwtService.generateToken(u.getEmail(), Map.of("userId", u.getId()));
        return new AuthResponse(token);
    }
}
